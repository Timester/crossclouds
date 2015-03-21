package net.talqum.crossclouds.providers.azure.blobstore;

import net.talqum.crossclouds.blobstorage.common.BlobStoreContext;

/**
 * Created by Imre on 2015.03.04..
 */
public interface AzureBlobStoreContext extends BlobStoreContext {

    @Override
    AzureBlobStore getBlobStore();
}
