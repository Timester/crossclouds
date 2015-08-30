package net.talqum.crossclouds.providers.google.cloudstorage;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStore;
import net.talqum.crossclouds.blobstorage.common.Blob;
import net.talqum.crossclouds.blobstorage.common.DefaultBlob;
import net.talqum.crossclouds.blobstorage.common.Payload;
import net.talqum.crossclouds.blobstorage.payloads.FilePayload;
import net.talqum.crossclouds.exceptions.ClientErrorCodes;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.exceptions.ProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Imre on 2015.03.04..
 */
public class GoogleBlobStore extends AbstractBlobStore {

    final Logger log = LoggerFactory.getLogger(GoogleBlobStore.class);

    GoogleBlobStore(DefaultGoogleBlobStoreContext context) {
        super(context);
    }

    @Override
    public boolean containerExists(String container) throws ProviderException{
        try {
            Storage.Buckets.Get get = ((DefaultGoogleBlobStoreContext) context).getClient().buckets().get(container);
            get.setProjection("full");
            Bucket bucket = get.execute();

            if (bucket != null) {
                if(!Strings.isNullOrEmpty(bucket.getName())){
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            if(((GoogleJsonResponseException)e).getStatusCode() == 404) {
                return false;
            } else {
                throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
            }
        }
    }

    @Override
    public boolean createContainer(String container) throws ProviderException {
        if (containerExists(container)) {
            return false;
        } else {
            Bucket newBucket = new Bucket();
            newBucket.setName(container);
            try {
                Storage.Buckets.Insert insert = ((DefaultGoogleBlobStoreContext) context).getClient().buckets()
                        .insert(((DefaultGoogleBlobStoreContext) context).applicationName, newBucket);

                insert.execute();

                return true;
            } catch (IOException e) {
                throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
            }
        }
    }

    @Override
    public Set<String> listContainerContent(String container) throws ProviderException {
        Set<String> returnValue = new HashSet<>();
        try {
            Storage.Objects.List listObjects = ((DefaultGoogleBlobStoreContext) context).getClient().objects().list(container);
            com.google.api.services.storage.model.Objects objects;
            do {
                objects = listObjects.execute();
                List<StorageObject> items = objects.getItems();
                if (null == items) {
                    log.debug("The bucket is empty");
                    break;
                }

                returnValue.addAll(items.stream().map(StorageObject::getName).collect(Collectors.toList()));

                listObjects.setPageToken(objects.getNextPageToken());
            } while (null != objects.getNextPageToken());

            return returnValue;
        } catch (Exception e) {
            if(((GoogleJsonResponseException)e).getStatusCode() == 404) {
                return returnValue;
            } else {
                throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
            }
        }
    }

    @Override
    public void deleteContainer(String container) throws ProviderException {
        try {
            Storage.Buckets.Delete delete = ((DefaultGoogleBlobStoreContext) context).getClient().buckets()
                    .delete(container);

            delete.execute();
        } catch (IOException e) {
            if(((GoogleJsonResponseException)e).getStatusCode() != 404) {
                throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
            }
        }
    }

    @Override
    public boolean blobExists(String container, String blobName) throws ProviderException {
        try {
            Storage.Objects.Get get = ((DefaultGoogleBlobStoreContext) context).getClient().objects().get(container, blobName);
            get.execute();

            return true;
        } catch (IOException e) {
            if(((GoogleJsonResponseException)e).getStatusCode() == 404) {
                return false;
            } else {
                throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
            }
        }
    }

    @Override
    public boolean putBlob(String container, Blob blob) throws ProviderException {
        if(!containerExists(container)) {
            createContainer(container);
        }

        try {
            StorageObject metaData = new StorageObject()
                    .setName(blob.getName())
                    .setContentDisposition("attachment");

            InputStreamContent mediaContent = new InputStreamContent(blob.getPayload().getContentType().toString(), blob.getPayload().openStream());

            Storage.Objects.Insert insert = ((DefaultGoogleBlobStoreContext) context).getClient().objects()
                    .insert(container, metaData,mediaContent);

            insert.setName(blob.getName());

            if (mediaContent.getLength() > 0 && mediaContent.getLength() <= 2 * 1000 * 1000 /* 2MB */) {
                insert.getMediaHttpUploader().setDirectUploadEnabled(true);
            }

            insert.execute();

            return true;
        } catch (IOException e) {
            throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
        }
    }

    @Override
    public Blob getBlob(String container, String blobName) throws ProviderException {
        try {
            Storage.Objects.Get get = ((DefaultGoogleBlobStoreContext) context).getClient().objects()
                    .get(container, blobName);

            File f = File.createTempFile(blobName.substring(0, blobName.lastIndexOf('.')),
                    blobName.substring(blobName.lastIndexOf('.')));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            get.getMediaHttpDownloader().setDirectDownloadEnabled(true);
            get.executeMediaAndDownloadTo(out);

            FileOutputStream fos = new FileOutputStream(f);
            out.writeTo(fos);

            Payload p = new FilePayload(f);
            return new DefaultBlob(blobName, p);
        } catch (IOException e) {
            if(((GoogleJsonResponseException)e).getStatusCode() == 404) {
                return null;
            } else {
                throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
            }
        }
    }

    @Override
    public void removeBlob(String container, String blobName) throws ProviderException {
        try {
            Storage.Objects.Delete delete = ((DefaultGoogleBlobStoreContext) context).getClient().objects()
                    .delete(container, blobName);

            delete.execute();

        } catch (IOException e) {
            if(((GoogleJsonResponseException)e).getStatusCode() != 404) {
                throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
            }
        }
    }

    @Override
    public long countBlobs(String container) throws ClientException {
        long retVal = 0;
        try {
            Storage.Objects.List listObjects = ((DefaultGoogleBlobStoreContext) context).getClient().objects().list(container);
            com.google.api.services.storage.model.Objects objects;
            do {
                objects = listObjects.execute();
                List<StorageObject> items = objects.getItems();
                if (null == items) {
                    return 0;
                } else {
                    retVal += items.size();
                }

                listObjects.setPageToken(objects.getNextPageToken());
            } while (null != objects.getNextPageToken());

            return retVal;
        } catch (IOException e) {
            if(((GoogleJsonResponseException)e).getStatusCode() == 404) {
                return 0;
            } else {
                throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
            }
        }
    }

}