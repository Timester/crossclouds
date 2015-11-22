package net.talqum.crossclouds.providers.azure.blobstore;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStoreContext;
import net.talqum.crossclouds.providers.ContextConfig;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

public class DefaultAzureBlobStoreContext extends AbstractBlobStoreContext implements AzureBlobStoreContext {

    private final CloudBlobClient blobClient;

    public DefaultAzureBlobStoreContext(ContextConfig cfg) throws URISyntaxException, InvalidKeyException{
        super();
        String storageConnectionString = "DefaultEndpointsProtocol=http;" +
                "AccountName=" + cfg.getId() + ";" +
                "AccountKey=" + cfg.getSecret();

        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        this.blobClient = storageAccount.createCloudBlobClient();

        this.async = cfg.isAsync();
        this.location = cfg.getLocation();

        setBlobStore(new AzureBlobStore(this));
    }

    @Override
    public AzureBlobStore getBlobStore() {
        return AzureBlobStore.class.cast(super.getBlobStore());
    }

    public CloudBlobClient getClient(){
        return this.blobClient;
    }

    @Override
    public void close() throws IOException {
    }
}
