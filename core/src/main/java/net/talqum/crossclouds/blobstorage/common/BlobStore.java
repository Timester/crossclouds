package net.talqum.crossclouds.blobstorage.common;

import java.util.Set;

/**
 * Created by Imre on 2015.03.04..
 */
public interface BlobStore {
    BlobStoreContext getContext();

    /**
     * Returns true if the container exists, false otherwise
     * @param container container name to check
     * @return true if the container exists, false otherwise
     */
    boolean containerExists(String container);

    /**
     * Creates the container if it does not exist
     * @param container the name of the created container
     * @return true if the container was nonexistent and successfully created, false otherwise
     */
    boolean createContainer(String container);

    Set<String> listContainerContent(String container);

    void clearContainer(String container);

    void deleteContainer(String container);

    boolean deleteContainerIfEmpty(String container);

    boolean blobExists(String container, String name);

    /**
     * Puts a Blob in the given container. If the container does not exist create it.
     * @param container name of the container to put the Blob in
     * @param blob the Blob to upload
     * @return true if the upload was successful, false otherwise
     */
    boolean putBlob(String container, Blob blob);

    Blob getBlob(String container, String name);

    void removeBlob(String container, String name);

    void removeBlobs(String container, Iterable<String> names);

    long countBlobs(String container);

}
