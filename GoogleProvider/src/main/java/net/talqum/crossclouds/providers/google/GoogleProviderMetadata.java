package net.talqum.crossclouds.providers.google;

import net.talqum.crossclouds.common.AbstractProviderMetadata;
import net.talqum.crossclouds.providers.google.cloudstorage.DefaultGoogleBlobStoreContext;

import static net.talqum.crossclouds.util.reflect.TypeConverter.typeToken;

/**
 * Created by Imre on 2015.03.07..
 */
public class GoogleProviderMetadata extends AbstractProviderMetadata {

    public GoogleProviderMetadata() {
        super("google");
        services.add(typeToken(DefaultGoogleBlobStoreContext.class));
    }
}
