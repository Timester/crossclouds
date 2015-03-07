package net.talqum.crossclouds.providers;

import net.talqum.crossclouds.common.ProviderMetadata;

import java.util.ServiceLoader;

/**
 * Created by Imre on 2015.03.07..
 */
public class ProviderLoader {

    public Iterable<ProviderMetadata> fromServiceLoader() {
        return ServiceLoader.load(ProviderMetadata.class);
    }


}
