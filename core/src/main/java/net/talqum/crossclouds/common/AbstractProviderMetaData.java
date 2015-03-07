package net.talqum.crossclouds.common;

import com.google.common.reflect.TypeToken;
import net.talqum.crossclouds.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Imre on 2015.03.07..
 */
public abstract class AbstractProviderMetadata implements ProviderMetadata {
    private String id;
    protected List<TypeToken<? extends Service>> services = new ArrayList<>();

    public AbstractProviderMetadata(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<TypeToken<? extends Service>> getServices() {
        return services;
    }
}
