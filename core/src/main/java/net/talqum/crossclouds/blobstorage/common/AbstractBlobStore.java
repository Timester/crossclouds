package net.talqum.crossclouds.blobstorage.common;

import net.talqum.crossclouds.exceptions.ClientException;
import org.slf4j.Logger;

import java.util.Set;

/**
 * Created by Imre on 2015.03.04..
 */
public abstract class AbstractBlobStore implements BlobStore {

    protected final Logger log;
    protected final BlobStoreContext context;

    protected AbstractBlobStore(BlobStoreContext context, Logger log) {
        this.context = context;
        this.log = log;
    }

    @Override
    public BlobStoreContext getContext() {
        return context;
    }

    @Override
    public boolean deleteContainerIfEmpty(String container) throws ClientException {
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

    protected String logContainerNotFound(String container) {
        return "Container " + container + " not found";
    }

    protected String logBlobNotFound(String container, String blob) {
        return "No key \"" + blob + "\" found in container \"" + container + "\"";
    }
}
