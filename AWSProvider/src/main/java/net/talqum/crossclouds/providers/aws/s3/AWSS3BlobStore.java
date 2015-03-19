package net.talqum.crossclouds.providers.aws.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import net.talqum.crossclouds.blobstorage.common.Blob;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStore;

import java.util.List;
import java.util.Set;

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
    public boolean createContainer(String container) {
        if(!containerExists(container)) {
            ((DefaultAWSS3BlobStoreContext) context).getS3Client().createBucket(new CreateBucketRequest(container));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Set<String> listContainerContent(String container) {
        return null;
    }

    @Override
    public void clearContainer(String container) {

    }

    @Override
    public void deleteContainer(String container) {

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

        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(blob.getPayload().getContentLength());
            ((DefaultAWSS3BlobStoreContext) context)
                    .getS3Client()
                    .putObject(new PutObjectRequest(container, blob.getName(),
                            blob.getPayload().openStream(),
                            objectMetadata));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Blob getBlob(String container, String blobName) {
        return null;
    }

    @Override
    public void removeBlob(String container, String blobName) {
        ((DefaultAWSS3BlobStoreContext) context).getS3Client().deleteObject(container, blobName);
    }

    @Override
    public long countBlobs(String container) {
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
    }

}