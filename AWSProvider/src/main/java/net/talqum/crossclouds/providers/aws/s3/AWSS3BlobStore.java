package net.talqum.crossclouds.providers.aws.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import net.talqum.crossclouds.blobstorage.common.Blob;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStore;
import net.talqum.crossclouds.blobstorage.common.DefaultBlob;
import net.talqum.crossclouds.blobstorage.common.Payload;
import net.talqum.crossclouds.blobstorage.payloads.FilePayload;

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
    }

    @Override
    public void clearContainer(String container) {
        // TODO finish
    }

    @Override
    public void deleteContainer(String container) {
        ((DefaultAWSS3BlobStoreContext) context).getS3Client().deleteBucket(container);
    }

    @Override
    public boolean deleteContainerIfEmpty(String container) {
        // TODO finish
        return false;
    }

    @Override
    public boolean blobExists(String container, String blobName) {
        S3Object s3Object = ((DefaultAWSS3BlobStoreContext) context).getS3Client().getObject(container, blobName);

        return s3Object == null;
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
        S3Object s3Object = ((DefaultAWSS3BlobStoreContext) context).getS3Client().getObject(container, blobName);

        ObjectMetadata objectMetadata = s3Object.getObjectMetadata();
        String contentType = objectMetadata.getContentType();

        switch (contentType){
            case "image/jpeg": {
                File f = new File(s3Object.getKey());
                try {
                    OutputStream os = new FileOutputStream(f);

                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while ((read = s3Object.getObjectContent().read(bytes)) != -1) {
                        os.write(bytes, 0, read);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Payload p = new FilePayload(f);
                return new DefaultBlob(blobName, p);
            }
        }

        return null;
    }

    @Override
    public void removeBlob(String container, String blobName) {
        try {
            ((DefaultAWSS3BlobStoreContext) context).getS3Client().deleteObject(container, blobName);
        }catch (Exception e){
            e.printStackTrace();
        }
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