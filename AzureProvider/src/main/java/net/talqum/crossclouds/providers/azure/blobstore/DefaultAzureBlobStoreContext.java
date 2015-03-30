package net.talqum.crossclouds.providers.azure.blobstore;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStoreContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by Imre on 2015.03.04..
 */
public class DefaultAzureBlobStoreContext extends AbstractBlobStoreContext implements AzureBlobStoreContext {

    private final CloudBlobClient blobClient;

    public DefaultAzureBlobStoreContext(String identity, String secret) throws URISyntaxException, InvalidKeyException{
        super();
        String storageConnectionString = "DefaultEndpointsProtocol=http;" +
                "AccountName=" + identity + ";" +
                "AccountKey=" + secret;

        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        this.blobClient = storageAccount.createCloudBlobClient();

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
