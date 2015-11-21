package net.talqum.crossclouds.providers.google;

import net.talqum.crossclouds.common.AbstractProviderMetadata;
import net.talqum.crossclouds.providers.google.cloudstorage.DefaultGoogleBlobStoreContext;
import net.talqum.crossclouds.providers.google.vm.DefaultGoogleComputeEngineContext;

import static net.talqum.crossclouds.util.reflect.TypeConverter.typeToken;

public class GoogleProviderMetadata extends AbstractProviderMetadata {

    public GoogleProviderMetadata() {
        super("google");
        services.add(typeToken(DefaultGoogleBlobStoreContext.class));
        services.add(typeToken(DefaultGoogleComputeEngineContext.class));
    }
}
