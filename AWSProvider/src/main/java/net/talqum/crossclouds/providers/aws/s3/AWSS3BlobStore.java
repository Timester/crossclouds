package net.talqum.crossclouds.providers.aws.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import net.talqum.crossclouds.blobstorage.common.Blob;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStore;
import net.talqum.crossclouds.blobstorage.common.DefaultBlob;
import net.talqum.crossclouds.blobstorage.common.Payload;
import net.talqum.crossclouds.blobstorage.payloads.FilePayload;
import net.talqum.crossclouds.exceptions.ClientErrorCodes;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.exceptions.ProviderException;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Imre on 2015.03.04..
 */
public class AWSS3BlobStore extends AbstractBlobStore {

    AWSS3BlobStore(DefaultAWSS3BlobStoreContext context) {
        super(context);
    }

    @Override
    public boolean containerExists(String container) {
        return ((DefaultAWSS3BlobStoreContext)context).getS3Client().doesBucketExist(container);
    }

    @Override
    public boolean createContainer(String container) throws ClientException {
        try{
            if(!containerExists(container)) {
                ((DefaultAWSS3BlobStoreContext) context).getS3Client().createBucket(new CreateBucketRequest(container));
                return true;
            } else {
                return false;
            }
        } catch (AmazonServiceException ase) {
            throw new ProviderException(ase.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        } catch (AmazonClientException ace){
            throw new ClientException(ace.getMessage(), ClientErrorCodes.NONETWORK);
        }
    }

    @Override
    public Set<String> listContainerContent(String container) throws ClientException {
        try{
            AmazonS3Client client = ((DefaultAWSS3BlobStoreContext) context).getS3Client();
            ObjectListing objectListing = client.listObjects(container);
            List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
            Set<String> names = new HashSet<>();

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
            throw new ProviderException(ase.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        } catch (AmazonClientException ace){
            throw new ClientException(ace.getMessage(), ClientErrorCodes.NONETWORK);
        }
    }

    @Override
    public void deleteContainer(String container) throws ClientException {
        try{
            ((DefaultAWSS3BlobStoreContext) context).getS3Client().deleteBucket(container);
        } catch (AmazonServiceException ase) {
            throw new ProviderException(ase.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        } catch (AmazonClientException ace){
            throw new ClientException(ace.getMessage(), ClientErrorCodes.NONETWORK);
        }
    }

    @Override
    public boolean blobExists(String container, String blobName) throws ClientException {
        try(S3Object s3Object = ((DefaultAWSS3BlobStoreContext) context).getS3Client().getObject(container, blobName)) {
            return true;
        } catch (AmazonS3Exception s3Exception){
            switch (s3Exception.getErrorCode()){
                case "NoSuchKey":
                    return false;
                default:
                    return false;
            }
        } catch (IOException e){
            throw new ClientException(e.getMessage(), ClientErrorCodes.UNKNOWN);
        }

    }

    @Override
    public boolean putBlob(String container, Blob blob) throws ClientException {
        if(!containerExists(container)) {
            createContainer(container);
        }

        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(blob.getPayload().getContentLength());
            ((DefaultAWSS3BlobStoreContext) context)
                    .getS3Client()
                    .putObject(new PutObjectRequest(container, blob.getName(),
                            blob.getPayload().openStream(),
                            objectMetadata));

            return true;
        } catch (AmazonServiceException ase) {
            throw new ProviderException(ase.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        } catch (AmazonClientException ace){
            throw new ClientException(ace.getMessage(), ClientErrorCodes.NONETWORK);
        } catch (IOException ioe){
            ioe.printStackTrace();
            return false;
        }
    }

    @Override
    public Blob getBlob(String container, String blobName) {
        try(S3Object s3Object = ((DefaultAWSS3BlobStoreContext) context).getS3Client().getObject(container, blobName)) {

            File f = File.createTempFile(blobName.substring(0, blobName.lastIndexOf('.')),
                    blobName.substring(blobName.lastIndexOf('.')));

            try (OutputStream os = new FileOutputStream(f)) {
                int read;
                byte[] bytes = new byte[1024];

                while ((read = s3Object.getObjectContent().read(bytes)) != -1) {
                    os.write(bytes, 0, read);
                }

                Payload p = new FilePayload(f);
                return new DefaultBlob(blobName, p);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void removeBlob(String container, String blobName) throws ClientException {
        try {
            ((DefaultAWSS3BlobStoreContext) context).getS3Client().deleteObject(container, blobName);
        }catch (AmazonServiceException ase) {
            throw new ProviderException(ase.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        } catch (AmazonClientException ace){
            throw new ClientException(ace.getMessage(), ClientErrorCodes.NONETWORK);
        }
    }

    @Override
    public long countBlobs(String container) throws ClientException {
        try {
            AmazonS3Client client = ((DefaultAWSS3BlobStoreContext) context).getS3Client();
            ObjectListing objectListing = client.listObjects(container);
            long count = objectListing.getObjectSummaries().size();
            boolean truncated = objectListing.isTruncated();

            while(truncated){
                ObjectListing subListing = client.listNextBatchOfObjects(objectListing);
                count += subListing.getObjectSummaries().size();
                truncated = subListing.isTruncated();
            }

            return count;
        }catch (AmazonServiceException ase) {
            throw new ProviderException(ase.getMessage(), ClientErrorCodes.SERVICEUNAVAILABLE);
        } catch (AmazonClientException ace){
            throw new ClientException(ace.getMessage(), ClientErrorCodes.NONETWORK);
        }
    }

}