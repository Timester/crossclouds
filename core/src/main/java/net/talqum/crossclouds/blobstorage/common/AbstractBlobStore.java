package net.talqum.crossclouds.blobstorage.common;

import net.talqum.crossclouds.blobstorage.common.BlobStore;
import net.talqum.crossclouds.blobstorage.common.BlobStoreContext;


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
    public void removeBlobs(String container, Iterable<String> names) {
        for (String name : names) {
            removeBlob(container, name);
        }
    }
}
