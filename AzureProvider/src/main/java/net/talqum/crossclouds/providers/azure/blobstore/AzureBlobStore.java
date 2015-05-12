package net.talqum.crossclouds.providers.azure.blobstore;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Imre on 2015.03.04..
 */
public class AzureBlobStore extends AbstractBlobStore {

    AzureBlobStore(DefaultAzureBlobStoreContext context) {
        super(context);
    }

    @Override
    public boolean containerExists(String container) throws ClientException {
        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            return containerReference.exists();
        } catch (URISyntaxException e){
            throw new ClientException(e.getMessage(), ClientErrorCodes.NONETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        }
    }

    @Override
    public boolean createContainer(String container) throws ClientException {
        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            return containerReference.createIfNotExists();
        } catch (URISyntaxException e){
            throw new ClientException(e.getMessage(), ClientErrorCodes.NONETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        }
    }

    @Override
    public Set<String> listContainerContent(String container) throws ClientException {
        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);

            Set<String> content = new HashSet<>();
            for (ListBlobItem blobItem : containerReference.listBlobs()) {
                content.add(blobItem.getUri().toString());
            }
            return content;
        } catch (URISyntaxException e){
            throw new ClientException(e.getMessage(), ClientErrorCodes.NONETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        }
    }

    @Override
    public void deleteContainer(String container) throws ClientException {
        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            containerReference.deleteIfExists();
        } catch (URISyntaxException e){
            throw new ClientException(e.getMessage(), ClientErrorCodes.NONETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        }
    }

    @Override
    public boolean blobExists(String container, String blobName) throws ClientException {
        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            CloudBlockBlob blockBlobReference = containerReference.getBlockBlobReference(blobName);

            return blockBlobReference.exists();
        } catch (URISyntaxException e){
            throw new ClientException(e.getMessage(), ClientErrorCodes.NONETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        }
    }

    @Override
    public boolean putBlob(String container, Blob blob) throws ClientException {
        if(!containerExists(container)) {
            createContainer(container);
        }

        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            CloudBlockBlob blockBlobReference = containerReference.getBlockBlobReference(blob.getName());

            blockBlobReference.upload(blob.getPayload().openStream(), blob.getPayload().getContentLength());

            return true;
        } catch (URISyntaxException e){
            throw new ClientException(e.getMessage(), ClientErrorCodes.NONETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Blob getBlob(String container, String blobName) throws ClientException {
        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();
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
            e.printStackTrace();
            return null;
        } catch (StorageException e) {
            throw  new ProviderException(e.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        } catch (URISyntaxException e) {
            throw new ClientException(e.getMessage(), ClientErrorCodes.NONETWORK);
        }
    }

    @Override
    public void removeBlob(String container, String blobName) throws ClientException {
        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            CloudBlockBlob blockBlobReference = containerReference.getBlockBlobReference(blobName);

            blockBlobReference.deleteIfExists();
        } catch (URISyntaxException e){
            throw new ClientException(e.getMessage(), ClientErrorCodes.NONETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        }
    }

    @Override
    public long countBlobs(String container) throws ClientException {
        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            long counter = 0;
            for (ListBlobItem blobItem : containerReference.listBlobs()) {
                counter++;
            }
            return counter;
        } catch (URISyntaxException e){
            throw new ClientException(e.getMessage(), ClientErrorCodes.NONETWORK);
        } catch (StorageException e){
            throw  new ProviderException(e.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        }
    }

}