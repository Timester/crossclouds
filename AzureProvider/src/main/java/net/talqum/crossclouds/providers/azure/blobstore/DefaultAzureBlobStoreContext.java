package net.talqum.crossclouds.providers.azure.blobstore;

import net.talqum.crossclouds.blobstorage.common.AbstractBlobStoreContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import com.microsoft.azure.storage.*;

/**
 * Created by Imre on 2015.03.04..
 */
public class DefaultAzureBlobStoreContext extends AbstractBlobStoreContext implements AzureBlobStoreContext {

    private final CloudStorageAccount storageAccount;

    public DefaultAzureBlobStoreContext(String identity, String secret) throws URISyntaxException, InvalidKeyException{
        super();
        String storageConnectionString = "DefaultEndpointsProtocol=http;" +
                "AccountName=" + identity + ";" +
                "AccountKey=" + secret;

        this.storageAccount = CloudStorageAccount.parse(storageConnectionString);

        setBlobStore(new AzureBlobStore(this));
    }

    @Override
    public AzureBlobStore getBlobStore() {
        return AzureBlobStore.class.cast(super.getBlobStore());
    }


    @Override
    public void close() throws IOException {
        // TODO
    }
}
