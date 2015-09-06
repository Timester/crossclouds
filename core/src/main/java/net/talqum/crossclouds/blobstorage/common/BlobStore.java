package net.talqum.crossclouds.blobstorage.common;

import net.talqum.crossclouds.exceptions.ClientException;

import java.util.Set;

/**
 * Created by Imre on 2015.03.04..
 */
public interface BlobStore {
    BlobStoreContext getContext();

    /**
     * Returns true if the container does not exists, and available for you to use.
     *
     * In some clouds like Amazon S3, container names must be globally unique. This method returns false if the name is
     * available for you to use. Returns true if it is used, the method does not differentiate between you or someone
     * else owning the container. To check containers owned by you, use listContainers().
     *
     * @param container container name to check.
     * @return true if the container exists, false otherwise
     * @throws ClientException if any network or service error occures
     * (ProviderException if the problem is with the service itself)
     */
    boolean containerExists(String container);

    /**
     * Returns a list of container names owned by you.
     * @return a list of your containers names.
     */
    Set<String> listContainers();

    /**
     * Creates the container if it does not exist
     * @param container the name of the created container.
     * @return true if the container was nonexistent and successfully created, false otherwise
     * @throws ClientException if any network or service error occures
     * (ProviderException if the problem is with the service itself)
     */
    boolean createContainer(String container);

    /**
     * Returns a set of strings with the names of the items in this container.
     * @param container the name of the container.
     * @return a set of strings describing container content, empty set if the container is empty
     * @throws ClientException if any network or service error occures
     * (ProviderException if the problem is with the service itself)
     */
    Set<String> listContainerContent(String container);

    /**
     * Empties the given container, does nothing if it is nonexistent or already empty.
     * @param container name of the container.
     * @throws ClientException if any network or service error occures
     * (ProviderException if the problem is with the service itself)
     */
    void clearContainer(String container);

    /**
     * Deletes the given container.
     * @param container name of the container.
     * @throws ClientException if any network or service error occures
     * (ProviderException if the problem is with the service itself)
     */
    void deleteContainer(String container);

    /**
     * Deletes the given container if it's empty.
     * @param container name of the container.
     * @return returns true if the coontainer was empty, thus deleted and false if it was non empty and
     * deletion did not happen.
     * @throws ClientException if any network or service error occures
     * (ProviderException if the problem is with the service itself)
     */
    boolean deleteContainerIfEmpty(String container);

    /**
     * Checks if a given blob exists in a given container.
     * @param container name of the container
     * @param blobName name of the blob
     * @return true if the given blob exists, false otherwise
     * @throws ClientException if any network or service error occures
     * (ProviderException if the problem is with the service itself)
     */
    boolean blobExists(String container, String blobName);

    /**
     * Puts a Blob in the given container. If the container does not exist create it.
     * @param container name of the container to put the Blob in
     * @param blob the Blob to upload
     * @return true if the upload was successful, false otherwise
     * @throws ClientException if any network or service error occures
     * (ProviderException if the problem is with the service itself)
     */
    boolean putBlob(String container, Blob blob);

    /**
     * Gets a blob with a given name from the given container.
     * @param container name of the container.
     * @param blobName name of the Blob.
     * @return a Blob object if found, or null.
     * @throws ClientException if any network or service error occures
     * (ProviderException if the problem is with the service itself)
     */
    Blob getBlob(String container, String blobName);

    /**
     * Removes a blob from the given container.
     * @param container name of the container.
     * @param blobName name of the Blob.
     * @throws ClientException if any network or service error occures
     * (ProviderException if the problem is with the service itself)
     */
    void removeBlob(String container, String blobName);

    /**
     * Removes multiple blobs from a given container.
     * @param container  name of the container.
     * @param blobNames name of the Blob.
     * @throws ClientException if any network or service error occures
     * (ProviderException if the problem is with the service itself)
     */
    void removeBlobs(String container, Iterable<String> blobNames);

    /**
     * Counts blobs in a container.
     * @param container  name of the container.
     * @return number of blobs in a container.
     * @throws ClientException if any network or service error occures
     * (ProviderException if the problem is with the service itself)
     */
    long countBlobs(String container);
}
