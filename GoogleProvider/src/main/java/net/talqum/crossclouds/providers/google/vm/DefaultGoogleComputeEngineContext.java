package net.talqum.crossclouds.providers.google.vm;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.ComputeScopes;
import net.talqum.crossclouds.compute.common.AbstractComputeCloudContext;
import net.talqum.crossclouds.exceptions.ClientErrorCodes;
import net.talqum.crossclouds.exceptions.ClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;

public class DefaultGoogleComputeEngineContext extends AbstractComputeCloudContext implements GoogleComputeEngineContext {

    final Logger log = LoggerFactory.getLogger(DefaultGoogleComputeEngineContext.class);

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport httpTransport;

    private final Compute computeCloudClient;

    private final String credentialsFilePath;
    final String projectId;
    private final String serviceAccountID;

    public DefaultGoogleComputeEngineContext(String projectId, String serviceAccountID, String credentialsFilePath) {

        super();

        log.info("Initializing Google Compute Engine context");

        this.projectId = projectId;
        this.serviceAccountID = serviceAccountID;
        this.credentialsFilePath = credentialsFilePath;

        Credential credential = null;

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            Set<String> scopes = new HashSet<>();
            scopes.add(ComputeScopes.COMPUTE);
            scopes.add(ComputeScopes.DEVSTORAGE_FULL_CONTROL);

            credential = authorizeWithServiceAccount(scopes);
        } catch (GeneralSecurityException e) {
            log.error("Auth failure", e);
        } catch (IOException e) {
            throw new ClientException(e, ClientErrorCodes.IO_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error", e);
        }

        setComputeCloud(new GoogleComputeEngine(this));

        computeCloudClient = new Compute.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(projectId)
                .build();
    }

    @Override
    public GoogleComputeEngine getComputeCloud() {
        return GoogleComputeEngine.class.cast(super.getComputeCloud());
    }

    public Compute getClient() {
        return computeCloudClient;
    }

    @Override
    public void close() throws IOException {
        // TODO
    }

    private GoogleCredential authorizeWithServiceAccount(Set<String> scopes) throws Exception {

        return new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(serviceAccountID)
                .setServiceAccountPrivateKeyFromP12File(new File(credentialsFilePath))
                .setServiceAccountScopes(scopes)
                .build();
    }

}