package net.talqum.crossclouds.common;

import com.google.common.reflect.TypeToken;
import net.talqum.crossclouds.Service;

import java.util.Set;

/**
 * Created by Imre on 2015.03.07..
 */
public interface ProviderMetadata {

    public String getId();

    public Set<TypeToken<? extends Service>> getServices();
}
