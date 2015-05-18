package net.talqum.crossclouds.blobstorage.common;

import net.talqum.crossclouds.AbstractService;

/**
 * Created by Imre on 2015.03.04..
 */
public abstract class AbstractBlobStoreContext extends AbstractService implements BlobStoreContext {

    private BlobStore blobStore;

    protected AbstractBlobStoreContext() {}

    protected void setBlobStore(BlobStore bs){
        this.blobStore = bs;
    }

    @Override
    public BlobStore getBlobStore() {
        return blobStore;
    }
}
