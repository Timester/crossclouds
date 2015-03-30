package net.talqum.crossclouds.providers.azure.blobstore;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import net.talqum.crossclouds.blobstorage.common.Blob;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStore;

import java.io.IOException;
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
    public boolean containerExists(String container) {
        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            return containerReference.exists();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        } catch (StorageException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createContainer(String container) {
        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            return containerReference.createIfNotExists();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        } catch (StorageException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Set<String> listContainerContent(String container) {
        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);

            Set<String> content = new HashSet<>();
            for (ListBlobItem blobItem : containerReference.listBlobs()) {
                content.add(blobItem.getUri().toString());
            }
            return content;
        } catch (URISyntaxException | StorageException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteContainer(String container) {
        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            containerReference.deleteIfExists();
        } catch (URISyntaxException | StorageException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean blobExists(String container, String blobName) {
        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            CloudBlockBlob blockBlobReference = containerReference.getBlockBlobReference(blobName);

            return blockBlobReference.exists();

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        } catch (StorageException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean putBlob(String container, Blob blob) {
        if(!containerExists(container)) {
            createContainer(container);
        }

        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            CloudBlockBlob blockBlobReference = containerReference.getBlockBlobReference(blob.getName());

            blockBlobReference.upload(blob.getPayload().openStream(), blob.getPayload().getContentLength());

            return true;
        } catch (URISyntaxException | IOException | StorageException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Blob getBlob(String container, String blobName) {
        // TODO azure finish getBlob
        return null;
    }

    @Override
    public void removeBlob(String container, String blobName) {
        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            CloudBlockBlob blockBlobReference = containerReference.getBlockBlobReference(blobName);

            blockBlobReference.deleteIfExists();
        } catch (URISyntaxException | StorageException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long countBlobs(String container) {

        CloudBlobClient client = ((DefaultAzureBlobStoreContext) context).getClient();

        try {
            CloudBlobContainer containerReference = client.getContainerReference(container);
            long counter = 0;
            for (ListBlobItem blobItem : containerReference.listBlobs()) {
                counter++;
            }
            return counter;
        } catch (URISyntaxException | StorageException e) {
            e.printStackTrace();
        }

        return 0;
    }

}