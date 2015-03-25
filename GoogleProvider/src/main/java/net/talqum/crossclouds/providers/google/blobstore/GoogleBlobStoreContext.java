package net.talqum.crossclouds.providers.google.blobstore;

import net.talqum.crossclouds.blobstorage.common.BlobStoreContext;

/**
 * Created by Imre on 2015.03.04..
 */
public interface GoogleBlobStoreContext extends BlobStoreContext {

    @Override
    GoogleBlobStore getBlobStore();
}
