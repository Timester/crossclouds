package net.talqum.crossclouds.providers.azure.blobstore;

import net.talqum.crossclouds.blobstorage.common.Blob;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStore;

import java.util.Set;

/**
 * Created by Imre on 2015.03.04..
 */
public class AzureBlobStore extends AbstractBlobStore {

    AzureBlobStore(DefaultAzureBlobStoreContext context) {
        super(context);
    }

    @Override
    public boolean containerExists(String container) {
        //TODO
        return false;
    }

    @Override
    public boolean createContainer(String container) {
        if(!containerExists(container)) {

            // TODO
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Set<String> listContainerContent(String container) {
        // TODO
        return null;
    }

    @Override
    public void clearContainer(String container) {

    }

    @Override
    public void deleteContainer(String container) {
        // TODO
    }

    @Override
    public boolean deleteContainerIfEmpty(String container) {
        return false;
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

        // TODO
        return false;
    }

    @Override
    public Blob getBlob(String container, String blobName) {

        return null;
    }

    @Override
    public void removeBlob(String container, String blobName) {

    }

    @Override
    public long countBlobs(String container) {

        // TODO
        return 0;
    }

}