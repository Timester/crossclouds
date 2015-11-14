package net.talqum.crossclouds.providers.aws.s3;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStoreContext;

import java.io.IOException;

public class DefaultAWSS3BlobStoreContext extends AbstractBlobStoreContext implements AWSS3BlobStoreContext {

    private final AmazonS3Client s3Client;

    public DefaultAWSS3BlobStoreContext(String identity, String secret) {
        super();
        setBlobStore(new AWSS3BlobStore(this));

        s3Client = new AmazonS3Client(new BasicAWSCredentials(identity, secret));
    }

    @Override
    public AWSS3BlobStore getBlobStore() {
        return AWSS3BlobStore.class.cast(super.getBlobStore());
    }

    public AmazonS3Client getClient() {
        return s3Client;
    }

    @Override
    public void close() throws IOException {
        s3Client.shutdown();
    }
}
