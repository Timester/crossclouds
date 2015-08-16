package net.talqum.crossclouds.providers.google.cloudstorage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStoreContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Imre on 2015.03.04..
 */
public class DefaultGoogleBlobStoreContext extends AbstractBlobStoreContext implements GoogleBlobStoreContext {

    private static String CREDENTIALS_FILE_PATH = "google_secrets.json";
    private static FileDataStoreFactory dataStoreFactory;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport httpTransport;

    private final Storage cloudStorageClient;

    public DefaultGoogleBlobStoreContext(String appName, String credentialsFilePath) throws URISyntaxException, InvalidKeyException{
        super();

        Credential credential = null;
        CREDENTIALS_FILE_PATH = credentialsFilePath;

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(new File("."));

            credential = authorize();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        cloudStorageClient = new Storage.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(appName).build();
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

    private static Credential authorize() throws Exception {
        // Load client secrets.
        GoogleClientSecrets clientSecrets = null;
        try {
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                    new InputStreamReader(DefaultGoogleBlobStoreContext.class.getResourceAsStream(
                            String.format("/%s", CREDENTIALS_FILE_PATH)
                    )));
            if (clientSecrets.getDetails().getClientId() == null ||
                    clientSecrets.getDetails().getClientSecret() == null) {
                throw new Exception("client_secrets not well formed.");
            }
        } catch (Exception e) {
            System.out.println("Problem loading client_secrets.json file. Make sure it exists, you are " +
                    "loading it with the right path, and a client ID and client secret are " +
                    "defined in it.\n" + e.getMessage());
            System.exit(1);
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
