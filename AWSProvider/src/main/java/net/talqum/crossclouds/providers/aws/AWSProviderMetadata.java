package net.talqum.crossclouds.providers.aws;

import net.talqum.crossclouds.common.AbstractProviderMetaData;
import net.talqum.crossclouds.providers.aws.s3.DefaultAWSS3BlobStoreContext;

import static net.talqum.crossclouds.util.reflect.TypeConverter.typeToken;

/**
 * Created by Imre on 2015.03.07..
 */
public class AWSProviderMetadata extends AbstractProviderMetaData {

    public AWSProviderMetadata(String id) {
        super(id);
        services.add(typeToken(DefaultAWSS3BlobStoreContext.class));
    }
}
