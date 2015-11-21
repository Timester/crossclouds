package net.talqum.crossclouds.common;

import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractProviderMetadata implements ProviderMetadata {
    private final String id;
    protected final List<TypeToken<? extends Context>> services;

    public AbstractProviderMetadata(String id) {
        this.id = id;
        this.services = new ArrayList<>();
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
