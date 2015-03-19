package net.talqum.crossclouds.blobstorage.common;

/**
 * Created by Imre on 2015.03.04..
 */
public abstract class AbstractBlobStore implements BlobStore {

    protected final BlobStoreContext context;

    protected AbstractBlobStore(BlobStoreContext context) {
        this.context = context;
    }

    @Override
    public BlobStoreContext getContext() {
        return context;
    }

    @Override
    public boolean deleteContainerIfEmpty(String container){
        if(countBlobs(container) == 0){
            deleteContainer(container);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void removeBlobs(String container, Iterable<String> blobNames) {
        for (String name : blobNames) {
            removeBlob(container, name);
        }
    }
}
