package net.talqum.crossclouds.blobstorage.common;

/**
 * Created by Imre on 2015.03.04..
 */
public abstract class AbstractBlobStoreContext implements BlobStoreContext {

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
