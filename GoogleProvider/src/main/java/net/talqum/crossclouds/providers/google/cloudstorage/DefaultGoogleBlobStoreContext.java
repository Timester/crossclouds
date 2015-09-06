package net.talqum.crossclouds.providers.google.cloudstorage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStoreContext;
import net.talqum.crossclouds.exceptions.ClientErrorCodes;
import net.talqum.crossclouds.exceptions.ClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Imre on 2015.03.04..
 */
public class DefaultGoogleBlobStoreContext extends AbstractBlobStoreContext implements GoogleBlobStoreContext {

    final Logger log = LoggerFactory.getLogger(DefaultGoogleBlobStoreContext.class);

    private static MemoryDataStoreFactory dataStoreFactory;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport httpTransport;

    private final Storage cloudStorageClient;

    private final String credentialsFilePath;
    final String applicationName;
    private final String serviceAccountID;

    public DefaultGoogleBlobStoreContext(String applicationName, String serviceAccountID, String credentialsFilePath)
            throws URISyntaxException, InvalidKeyException{

        super();

        log.info("Initializing Google Blobstorecontext");

        this.applicationName = applicationName;
        this.serviceAccountID = serviceAccountID;
        this.credentialsFilePath = credentialsFilePath;

        Credential credential = null;

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new MemoryDataStoreFactory();

            credential = authorizeWithServiceAccount();
        } catch (GeneralSecurityException e) {
            log.error("Auth failure", e);
        } catch (IOException e) {
            throw new ClientException(e, ClientErrorCodes.IO_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error", e);
        }

        setBlobStore(new GoogleBlobStore(this));

        cloudStorageClient = new Storage.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .build();
    }

    @Override
    public GoogleBlobStore getBlobStore() {
        return GoogleBlobStore.class.cast(super.getBlobStore());
    }

    public Storage getClient() {
        return cloudStorageClient;
    }

    @Override
    public void close() throws IOException {
        // TODO
    }

    private GoogleCredential authorizeWithServiceAccount() throws Exception {
        Set<String> scopes = new HashSet<>();
        scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(serviceAccountID)
                .setServiceAccountPrivateKeyFromP12File(new File(credentialsFilePath))
                .setServiceAccountScopes(Collections.singleton(StorageScopes.DEVSTORAGE_FULL_CONTROL))
                .build();

        return credential;
    }

    private Credential authorizeWithWebFlow() throws Exception {
        // Load client secrets.
        GoogleClientSecrets clientSecrets;
        try {
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                    new InputStreamReader(
                            DefaultGoogleBlobStoreContext.class.getResourceAsStream(
                                    String.format("/%s", credentialsFilePath)
                            )
                    )
            );

            if (clientSecrets.getDetails().getClientId() == null || clientSecrets.getDetails().getClientSecret() == null) {
                throw new Exception("client_secrets not well formed.");
            }

        } catch (Exception e) {
            throw new ClientException(ClientErrorCodes.UNKNOWN);
        }

        // Set up authorization code flow.
        Set<String> scopes = new HashSet<>();
        scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);
        scopes.add(StorageScopes.DEVSTORAGE_READ_ONLY);
        scopes.add(StorageScopes.DEVSTORAGE_READ_WRITE);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(dataStoreFactory)
                .build();
        // Authorize.
        VerificationCodeReceiver receiver = new LocalServerReceiver();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }


}
