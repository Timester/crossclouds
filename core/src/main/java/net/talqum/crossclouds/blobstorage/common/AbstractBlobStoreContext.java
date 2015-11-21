package net.talqum.crossclouds.blobstorage.common;

public abstract class AbstractBlobStoreContext implements BlobStoreContext {

    private BlobStore blobStore;
    protected boolean async;
    protected String location;

    protected AbstractBlobStoreContext() {}

    protected void setBlobStore(BlobStore bs){
        this.blobStore = bs;
    }

    @Override
    public BlobStore getBlobStore() {
        return blobStore;
    }
}
