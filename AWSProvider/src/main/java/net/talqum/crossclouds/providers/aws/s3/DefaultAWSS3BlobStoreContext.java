package net.talqum.crossclouds.providers.aws.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStoreContext;

import java.io.IOException;

/**
 * Created by Imre on 2015.03.04..
 */
public class DefaultAWSS3BlobStoreContext extends AbstractBlobStoreContext implements AWSS3BlobStoreContext {

    private final AmazonS3Client s3Client;

    public DefaultAWSS3BlobStoreContext(String identity, String secret) {
        super();
        setBlobStore(new AWSS3BlobStore(this));

        AWSCredentials creds = new BasicAWSCredentials(identity, secret);
        s3Client = new AmazonS3Client(creds);
    }

    @Override
    public AWSS3BlobStore getBlobStore() {
        return AWSS3BlobStore.class.cast(super.getBlobStore());
    }

    public AmazonS3Client getS3Client() {
        return s3Client;
    }

    @Override
    public void close() throws IOException {
        s3Client.shutdown();
    }
}
