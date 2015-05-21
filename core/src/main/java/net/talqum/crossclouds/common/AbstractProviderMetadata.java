package net.talqum.crossclouds.common;

import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Imre on 2015.03.07..
 */
public abstract class AbstractProviderMetadata implements ProviderMetadata {
    private String id;
    protected List<TypeToken<? extends Context>> services = new ArrayList<>();

    public AbstractProviderMetadata(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<TypeToken<? extends Context>> getServices() {
        return services;
    }
}
