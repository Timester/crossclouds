package net.talqum.crossclouds.providers.aws.s3;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import net.talqum.crossclouds.blobstorage.common.AbstractBlobStoreContext;
import net.talqum.crossclouds.providers.ContextConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

public class DefaultAWSS3BlobStoreContext extends AbstractBlobStoreContext implements AWSS3BlobStoreContext {

    final Logger log = LoggerFactory.getLogger(DefaultAWSS3BlobStoreContext.class);

    private final AmazonS3Client s3Client;

    public DefaultAWSS3BlobStoreContext(ContextConfig cfg) {
        super();
        setBlobStore(new AWSS3BlobStore(this));

        this.s3Client = new AmazonS3Client(new BasicAWSCredentials(cfg.getId(), cfg.getSecret()));

        if(!isNullOrEmpty(cfg.getLocation())){
            try {
                Regions regions = Regions.fromName(cfg.getLocation());

                this.s3Client.setRegion(Region.getRegion(regions));
            } catch (IllegalArgumentException e) {
                log.error("Invalid location: " + cfg.getLocation() + " using default: " + s3Client.getRegion().name());
            }
        }

        this.async = cfg.isAsync();
        this.location = cfg.getLocation();
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
