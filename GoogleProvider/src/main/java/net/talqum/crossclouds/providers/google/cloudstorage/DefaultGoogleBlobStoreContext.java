package net.talqum.crossclouds.providers.google.cloudstorage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStoreContext;
import net.talqum.crossclouds.exceptions.ClientErrorCodes;
import net.talqum.crossclouds.exceptions.ClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;

public class DefaultGoogleBlobStoreContext extends AbstractBlobStoreContext implements GoogleBlobStoreContext {

    final Logger log = LoggerFactory.getLogger(DefaultGoogleBlobStoreContext.class);

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport httpTransport;

    private final Storage cloudStorageClient;

    private final String credentialsFilePath;
    final String applicationName;
    private final String serviceAccountID;

    public DefaultGoogleBlobStoreContext(String applicationName, String serviceAccountID, String credentialsFilePath) {

        super();

        log.info("Initializing Google Blobstorecontext");

        this.applicationName = applicationName;
        this.serviceAccountID = serviceAccountID;
        this.credentialsFilePath = credentialsFilePath;

        Credential credential = null;

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
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

        return new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(serviceAccountID)
                .setServiceAccountPrivateKeyFromP12File(new File(credentialsFilePath))
                .setServiceAccountScopes(scopes)
                .build();
    }

}
