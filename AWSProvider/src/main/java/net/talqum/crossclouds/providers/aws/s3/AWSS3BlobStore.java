package net.talqum.crossclouds.providers.aws.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.model.*;
import net.talqum.crossclouds.blobstorage.common.Blob;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStore;
import net.talqum.crossclouds.blobstorage.common.DefaultBlob;
import net.talqum.crossclouds.blobstorage.common.Payload;
import net.talqum.crossclouds.blobstorage.payloads.FilePayload;
import net.talqum.crossclouds.exceptions.ClientErrorCodes;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.exceptions.ProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Imre on 2015.03.04..
 */
public class AWSS3BlobStore extends AbstractBlobStore {

    public static final String NO_SUCH_BUCKET = "NoSuchBucket";
    public static final String NO_SUCH_KEY = "NoSuchKey";
    public static final String OPERATION_ABORTED = "OperationAborted";

    final Logger log = LoggerFactory.getLogger(AWSS3BlobStore.class);

    AWSS3BlobStore(DefaultAWSS3BlobStoreContext context) {
        super(context);
    }

    @Override
    public boolean containerExists(String container) {
        return ((DefaultAWSS3BlobStoreContext)context).getClient().doesBucketExist(container);
    }

    @Override
    public boolean createContainer(String container) throws ClientException {
        try{
            if (containerExists(container)) {
                return false;
            } else {
                ((DefaultAWSS3BlobStoreContext) context).getClient().createBucket(new CreateBucketRequest(container));
                return true;
            }
        } catch (AmazonServiceException ase) {
            switch (ase.getErrorCode()){
                case OPERATION_ABORTED:
                    log.warn(ase.getErrorMessage());
                    throw new ProviderException(ase, ClientErrorCodes.OPERATION_CONFILCT);
                default:
                    throw new ProviderException(ase, ClientErrorCodes.SERVICE_UNAVAILABLE);
            }
        } catch (AmazonClientException ace){
            throw new ClientException(ace, ClientErrorCodes.NO_NETWORK);
        }
    }

    @Override
    public Set<String> listContainerContent(String container) throws ClientException {
        Set<String> names = new HashSet<>();
        try{
            AmazonS3Client client = ((DefaultAWSS3BlobStoreContext) context).getClient();
            ObjectListing objectListing = client.listObjects(container);
            List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();

            names.addAll(objectSummaries.stream().map(S3ObjectSummary::getKey).collect(Collectors.toList()));

            boolean truncated = objectListing.isTruncated();

            while(truncated){
                ObjectListing subListing = client.listNextBatchOfObjects(objectListing);
                objectSummaries = objectListing.getObjectSummaries();

                names.addAll(objectSummaries.stream().map(S3ObjectSummary::getKey).collect(Collectors.toList()));

                truncated = subListing.isTruncated();
            }

            return names;
        } catch (AmazonServiceException ase) {
            switch (ase.getErrorCode()){
                case NO_SUCH_BUCKET:
                    log.info("Bucket " + container + " not found");
                    return names;
                default:
                    throw new ProviderException(ase, ClientErrorCodes.SERVICE_UNAVAILABLE);
            }
        } catch (AmazonClientException ace){
            throw new ClientException(ace, ClientErrorCodes.NO_NETWORK);
        }
    }

    @Override
    public void deleteContainer(String container) throws ClientException {
        try {
            ((DefaultAWSS3BlobStoreContext) context).getClient().deleteBucket(container);
        } catch (AmazonServiceException ase) {
            switch (ase.getErrorCode()){
                case NO_SUCH_BUCKET:
                    log.info("Bucket " + container + " not found");
                    break;
                default:
                    throw new ProviderException(ase, ClientErrorCodes.SERVICE_UNAVAILABLE);
            }
        } catch (AmazonClientException ace){
            throw new ClientException(ace, ClientErrorCodes.NO_NETWORK);
        }
    }

    @Override
    public boolean blobExists(String container, String blobName) throws ClientException {
        try(S3Object s3Object = ((DefaultAWSS3BlobStoreContext) context).getClient().getObject(container, blobName)) {
            return true;
        } catch (AmazonS3Exception s3Exception){
            switch (s3Exception.getErrorCode()){
                case NO_SUCH_KEY:
                    log.info("No key \"" + blobName + "\" found in container \"" + container + "\"");
                    return false;
                case NO_SUCH_BUCKET:
                    log.info("Bucket " + container + " not found");
                    return false;
                default:
                    return false;
            }
        } catch (IOException e){
            throw new ClientException(e, ClientErrorCodes.UNKNOWN);
        }

    }

    @Override
    public boolean putBlob(String container, Blob blob) throws ClientException {
        if(!createContainer(container)) {
            log.info("Container \"" + container + "\" not found, now created" );
        }

        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(blob.getPayload().getContentLength());
            ((DefaultAWSS3BlobStoreContext) context)
                    .getClient()
                    .putObject(new PutObjectRequest(container, blob.getName(),
                            blob.getPayload().openStream(),
                            objectMetadata));

            return true;
        } catch (AmazonServiceException ase) {
            throw new ProviderException(ase, ClientErrorCodes.SERVICE_UNAVAILABLE);
        } catch (AmazonClientException ace){
            throw new ClientException(ace, ClientErrorCodes.NO_NETWORK);
        } catch (IOException ioe){
            throw new ClientException(ioe, ClientErrorCodes.IO_ERROR);
        }
    }

    @Override
    public Blob getBlob(String container, String blobName) throws ClientException {
        try(S3Object s3Object = ((DefaultAWSS3BlobStoreContext) context).getClient().getObject(container, blobName)) {
            File f = File.createTempFile(blobName.substring(0, blobName.lastIndexOf('.')),
                    blobName.substring(blobName.lastIndexOf('.')));

            try (OutputStream os = new FileOutputStream(f); S3ObjectInputStream s3ois = s3Object.getObjectContent()) {
                int read;
                byte[] bytes = new byte[1024];

                while ((read = s3ois.read(bytes)) != -1) {
                    os.write(bytes, 0, read);
                }

                Payload p = new FilePayload(f);
                return new DefaultBlob(blobName, p);
            }
        } catch (AmazonServiceException ase) {
            switch (ase.getErrorCode()){
                case NO_SUCH_KEY:
                    log.info("No key \"" + blobName + "\" found in container \"" + container + "\"");
                    return null;
                case NO_SUCH_BUCKET:
                    log.info("Bucket " + container + " not found");
                    return null;
                default:
                    throw new ProviderException(ase, ClientErrorCodes.SERVICE_UNAVAILABLE);
            }
        } catch (AmazonClientException ace){
            throw new ClientException(ace, ClientErrorCodes.NO_NETWORK);
        } catch (IOException e){
            throw new ClientException(e, ClientErrorCodes.IO_ERROR);
        }
    }

    @Override
    public void removeBlob(String container, String blobName) throws ClientException {
        try {
            ((DefaultAWSS3BlobStoreContext) context).getClient().deleteObject(container, blobName);
        } catch (AmazonServiceException ase) {
            switch (ase.getErrorCode()){
                case NO_SUCH_KEY:
                    log.info("No key \"" + blobName + "\" found in container \"" + container + "\"");
                    break;
                case NO_SUCH_BUCKET:
                    log.info("Bucket " + container + " not found");
                    break;
                default:
                    throw new ProviderException(ase, ClientErrorCodes.SERVICE_UNAVAILABLE);
            }
        } catch (AmazonClientException ace){
            throw new ClientException(ace, ClientErrorCodes.NO_NETWORK);
        }
    }

    @Override
    public long countBlobs(String container) throws ClientException {
        try {
            AmazonS3Client client = ((DefaultAWSS3BlobStoreContext) context).getClient();
            ObjectListing objectListing = client.listObjects(container);
            long count = objectListing.getObjectSummaries().size();
            boolean truncated = objectListing.isTruncated();

            while(truncated){
                ObjectListing subListing = client.listNextBatchOfObjects(objectListing);
                count += subListing.getObjectSummaries().size();
                truncated = subListing.isTruncated();
            }

            return count;
        } catch (AmazonS3Exception as3e) {
            switch (as3e.getErrorCode()){
                case NO_SUCH_BUCKET:
                    log.info("Bucket " + container + " not found");
                    return 0;
                default:
                    throw new ProviderException(as3e, ClientErrorCodes.SERVICE_UNAVAILABLE);
            }
        } catch (AmazonServiceException ase) {
            throw new ProviderException(ase, ClientErrorCodes.SERVICE_UNAVAILABLE);
        } catch (AmazonClientException ace) {
            throw new ClientException(ace, ClientErrorCodes.NO_NETWORK);
        }
    }

}