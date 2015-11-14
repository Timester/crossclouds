package net.talqum.crossclouds.providers.aws;

import net.talqum.crossclouds.common.AbstractProviderMetadata;
import net.talqum.crossclouds.providers.aws.ec2.DefaultAWSEC2ComputeCloudContext;
import net.talqum.crossclouds.providers.aws.s3.DefaultAWSS3BlobStoreContext;

import static net.talqum.crossclouds.util.reflect.TypeConverter.typeToken;

public class AWSProviderMetadata extends AbstractProviderMetadata {

    public AWSProviderMetadata() {
        super("aws");
        services.add(typeToken(DefaultAWSS3BlobStoreContext.class));
        services.add(typeToken(DefaultAWSEC2ComputeCloudContext.class));
    }
}
