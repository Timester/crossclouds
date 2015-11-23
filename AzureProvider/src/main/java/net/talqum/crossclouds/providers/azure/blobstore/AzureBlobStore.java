package net.talqum.crossclouds.providers.azure.blobstore;

import com.google.common.collect.Lists;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import net.talqum.crossclouds.blobstorage.common.Blob;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStore;
import net.talqum.crossclouds.blobstorage.common.DefaultBlob;
import net.talqum.crossclouds.blobstorage.common.Payload;
import net.talqum.crossclouds.blobstorage.payloads.FilePayload;
import net.talqum.crossclouds.exceptions.ClientErrorCodes;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.exceptions.ProviderException;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

public class AzureBlobStore extends AbstractBlobStore {
    
    private final CloudBlobClient client;

    AzureBlobStore(DefaultAzureBlobStoreContext context) {
        super(context, LoggerFactory.getLogger(AzureBlobStore.class));
        
        client = context.getClient();
    }

    @Override
    public boolean containerExists(String container) {
        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            return containerReference.exists();
        } catch (URISyntaxException e){
            throw new ClientException(e, ClientErrorCodes.NO_NETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e, ClientErrorCodes.SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public Set<String> listContainers() {
        Iterable<CloudBlobContainer> cloudBlobContainers = client.listContainers();

        return Lists.newArrayList(cloudBlobContainers)
                .stream()
                .map(CloudBlobContainer::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean createContainer(String container) {
        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            return containerReference.createIfNotExists();
        } catch (URISyntaxException e){
            throw new ClientException(e, ClientErrorCodes.NO_NETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e, ClientErrorCodes.SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public Set<String> listContainerContent(String container) {
        Set<String> content = new HashSet<>();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);

            for (ListBlobItem blobItem : containerReference.listBlobs()) {
                String URI = blobItem.getUri().toString();
                content.add(URI.substring(URI.lastIndexOf('/') + 1));
            }
            return content;
        } catch (NoSuchElementException nse) {
            logContainerNotFound(container);
            return content;
        } catch (URISyntaxException e){
            throw new ClientException(e, ClientErrorCodes.NO_NETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e, ClientErrorCodes.SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public void deleteContainer(String container) {
        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            containerReference.deleteIfExists();
        } catch (NoSuchElementException nse) {
            logContainerNotFound(container);
        } catch (URISyntaxException e){
            throw new ClientException(e, ClientErrorCodes.NO_NETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e, ClientErrorCodes.SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public boolean blobExists(String container, String blobName) {
        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            CloudBlockBlob blockBlobReference = containerReference.getBlockBlobReference(blobName);

            return blockBlobReference.exists();
        } catch (NoSuchElementException nse) {
            logContainerNotFound(container);
            return false;
        } catch (URISyntaxException e){
            throw new ClientException(e, ClientErrorCodes.NO_NETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e, ClientErrorCodes.SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public boolean putBlob(String container, Blob blob) {
        if(createContainer(container)) {
            log.info("Container \"" + container + "\" not found, now created");
        }

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            CloudBlockBlob blockBlobReference = containerReference.getBlockBlobReference(blob.getName());

            blockBlobReference.upload(blob.getPayload().openStream(), blob.getPayload().getContentLength());

            return true;
        } catch (URISyntaxException e){
            throw new ClientException(e, ClientErrorCodes.NO_NETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e, ClientErrorCodes.SERVICE_UNAVAILABLE);
        } catch (IOException e) {
            throw new ClientException(e, ClientErrorCodes.IO_ERROR);
        }
    }

    @Override
    public Blob getBlob(String container, String blobName) {
        try{
            CloudBlobContainer containerReference = client.getContainerReference(container);

            File f = File.createTempFile(blobName.substring(0, blobName.lastIndexOf('.')),
                    blobName.substring(blobName.lastIndexOf('.')));

            try (OutputStream os = new FileOutputStream(f)) {
                CloudBlockBlob blockBlobReference = containerReference.getBlockBlobReference(blobName);
                blockBlobReference.download(os);

                Payload p = new FilePayload(f);
                return new DefaultBlob(blobName, p);
            }
        } catch (IOException e){
            log.error("IOException occured", e);
            return null;
        } catch (StorageException e) {
            switch (e.getErrorCode()) {
                case "BlobNotFound":
                    logBlobNotFound(container, blobName);
                    return null;
                case "ContainerNotFound":
                    logContainerNotFound(container);
                    return null;
                default:
                    throw  new ProviderException(e, ClientErrorCodes.SERVICE_UNAVAILABLE);
            }
        } catch (URISyntaxException e) {
            throw new ClientException(e, ClientErrorCodes.NO_NETWORK);
        }
    }

    @Override
    public void removeBlob(String container, String blobName) {
        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            CloudBlockBlob blockBlobReference = containerReference.getBlockBlobReference(blobName);

            blockBlobReference.deleteIfExists();
        } catch (URISyntaxException e){
            throw new ClientException(e, ClientErrorCodes.NO_NETWORK);
        } catch (StorageException e){
            switch (e.getErrorCode()) {
                case "BlobNotFound":
                    logBlobNotFound(container, blobName);
                    break;
                case "ContainerNotFound":
                    logContainerNotFound(container);
                    break;
                default:
                    throw  new ProviderException(e, ClientErrorCodes.SERVICE_UNAVAILABLE);
            }
        }
    }

    @Override
    public long countBlobs(String container) {
        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            long counter = 0;
            for (ListBlobItem blobItem : containerReference.listBlobs()) {
                counter++;
            }
            return counter;
        } catch (NoSuchElementException nse) {
            logContainerNotFound(container);
            return 0;
        } catch (URISyntaxException e){
            throw new ClientException(e, ClientErrorCodes.NO_NETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e, ClientErrorCodes.SERVICE_UNAVAILABLE);
        }
    }

}