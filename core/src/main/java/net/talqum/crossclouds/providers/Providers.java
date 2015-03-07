package net.talqum.crossclouds.providers;

import net.talqum.crossclouds.common.ProviderMetadata;
import net.talqum.crossclouds.exceptions.ProviderNotFoundException;

import java.util.ServiceLoader;

/**
 * Created by Imre on 2015.03.07..
 */
public class Providers {

    private static Iterable<ProviderMetadata> loadProviderMetadata() {
        return ServiceLoader.load(ProviderMetadata.class);
    }

    public static ProviderMetadata find(String id) throws ProviderNotFoundException {
        Iterable<ProviderMetadata> providerMetadata = loadProviderMetadata();

        for (ProviderMetadata metadata : providerMetadata) {
            if(metadata.getId().equals(id)){
                return metadata;
            }
        }

        throw new ProviderNotFoundException();
    }
}
