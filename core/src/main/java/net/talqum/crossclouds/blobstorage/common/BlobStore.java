package net.talqum.crossclouds.blobstorage.common;

import net.talqum.crossclouds.blobstorage.exceptions.ContainerNotFoundException;

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

    /**
     * Returns a set of string with the names of the items in this blob.
     * @param container the name of the container
     * @throws net.talqum.crossclouds.blobstorage.exceptions.ContainerNotFoundException if the container is
     * nonexistent
     * @return a set of strings describing container content
     */
    Set<String> listContainerContent(String container);

    /**
     * Empties the given container.
     * @throws net.talqum.crossclouds.blobstorage.exceptions.ContainerNotFoundException if the container is
     * nonexistent
     * @param container
     */
    void clearContainer(String container) throws ContainerNotFoundException;

    /**
     * Deletes the given container.
     * @param container
     */
    void deleteContainer(String container);

    /**
     * Deletes the given container if it's empty.
     * @param container
     * @return returns true if the coontainer was empty, thus deleted and false if it was non empty and
     * deletion did not happen.
     */
    boolean deleteContainerIfEmpty(String container);

    /**
     * Checks if a given blob exists in a given container.
     * @param container name of the container
     * @param blobName name of the blob
     * @return
     */
    boolean blobExists(String container, String blobName);

    /**
     * Puts a Blob in the given container. If the container does not exist create it.
     * @param container name of the container to put the Blob in
     * @param blob the Blob to upload
     * @return true if the upload was successful, false otherwise
     */
    boolean putBlob(String container, Blob blob);

    /**
     * Gets a blob with a given name from the given container.
     * @param container name of the container.
     * @param blobName name of the Blob.
     * @return a Blob object if found, or null.
     */
    Blob getBlob(String container, String blobName);

    /**
     * Removes a blob from the given container.
     * @param container
     * @param blobName
     */
    void removeBlob(String container, String blobName);

    /**
     * Removes multiple blobs from a given container.
     * @param container
     * @param blobNames
     */
    void removeBlobs(String container, Iterable<String> blobNames);

    /**
     * Counts blobs in a container.
     * @param container
     * @return number of blobs in a container.
     */
    long countBlobs(String container);

}
