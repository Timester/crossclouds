package net.talqum.crossclouds.common;

import com.google.common.reflect.TypeToken;
import net.talqum.crossclouds.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Imre on 2015.03.07..
 */
public abstract class AbstractProviderMetaData implements ProviderMetadata {
    private String id;
    protected Set<TypeToken<? extends Service>> services = new HashSet<>();

    public AbstractProviderMetaData(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Set<TypeToken<? extends Service>> getServices() {
        return services;
    }
}
