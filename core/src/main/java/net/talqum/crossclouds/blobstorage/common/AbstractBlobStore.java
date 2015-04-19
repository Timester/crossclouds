package net.talqum.crossclouds.blobstorage.common;

import net.talqum.crossclouds.exceptions.ClientException;

import java.util.Set;

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
    public void clearContainer(String container) throws ClientException {
        Set<String> blobs = listContainerContent(container);

        removeBlobs(container, blobs);
    }

    @Override
    public void removeBlobs(String container, Iterable<String> blobNames) throws ClientException {
        for (String name : blobNames) {
            removeBlob(container, name);
        }
    }
}
