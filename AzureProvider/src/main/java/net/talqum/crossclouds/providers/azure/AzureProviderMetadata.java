package net.talqum.crossclouds.providers.azure;

import net.talqum.crossclouds.common.AbstractProviderMetadata;
import net.talqum.crossclouds.providers.azure.blobstore.DefaultAzureBlobStoreContext;

import static net.talqum.crossclouds.util.reflect.TypeConverter.typeToken;

/**
 * Created by Imre on 2015.03.07..
 */
public class AzureProviderMetadata extends AbstractProviderMetadata {

    public AzureProviderMetadata() {
        super("azure");
        services.add(typeToken(DefaultAzureBlobStoreContext.class));
    }
}
