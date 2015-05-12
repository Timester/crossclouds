package net.talqum.crossclouds.providers.google.blobstore;

import net.talqum.crossclouds.blobstorage.common.Blob;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStore;

import java.util.Set;

/**
 * Created by Imre on 2015.03.04..
 */
public class GoogleBlobStore extends AbstractBlobStore {

    GoogleBlobStore(DefaultGoogleBlobStoreContext context) {
        super(context);
    }

    @Override
    public boolean containerExists(String container) {
        return false;
    }

    @Override
    public boolean createContainer(String container) {
        return false;
    }

    @Override
    public Set<String> listContainerContent(String container) {
        return null;
    }

    @Override
    public void deleteContainer(String container) {

    }

    @Override
    public boolean blobExists(String container, String blobName) {
        return false;
    }

    @Override
    public boolean putBlob(String container, Blob blob) {
        if(!containerExists(container)) {
            createContainer(container);
        }

        return false;
    }

    @Override
    public Blob getBlob(String container, String blobName) {
        // TODO finish
        return null;
    }

    @Override
    public void removeBlob(String container, String blobName) {

    }

    @Override
    public long countBlobs(String container) {

        return 0;
    }

}