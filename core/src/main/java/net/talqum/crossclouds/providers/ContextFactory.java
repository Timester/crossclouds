package net.talqum.crossclouds.providers;

import com.google.common.reflect.TypeToken;
import net.talqum.crossclouds.Service;
import net.talqum.crossclouds.common.Context;
import net.talqum.crossclouds.common.ProviderMetadata;
import net.talqum.crossclouds.exceptions.ProviderNotFoundException;
import net.talqum.crossclouds.exceptions.ServiceNotSupportedException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static net.talqum.crossclouds.util.reflect.TypeConverter.typeToken;

/**
 * Created by Imre on 2015.03.07..
 */
public class ContextFactory {
    private String identity;
    private String secret;

    private ProviderMetadata providerMetadata;

    private ContextFactory(String provider) {
        this.providerMetadata = Providers.find(provider);
    }

    public static ContextFactory newFactory(String provider){
        return new ContextFactory(provider);
    }

    public ContextFactory credentials(String identity, String secret){
        this.identity = identity;
        this.secret = secret;
        return this;
    }

    public <C extends Context> C build(Class<C> contextType) {

        TypeToken<C> cTypeToken = typeToken(contextType);

        try {
            List<TypeToken<? extends Service>> services = providerMetadata.getServices();
            Class<?> rawType = null;

            for (TypeToken<? extends Service> serviceTypeToken : services) {
                if(cTypeToken.isAssignableFrom(serviceTypeToken)){
                    rawType = serviceTypeToken.getRawType();
                }
            }

            if (rawType != null) {
                return (C)rawType.getConstructor(String.class, String.class).newInstance(identity, secret);
            } else {
                throw new ServiceNotSupportedException("No service found for the given criteria.");
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new ServiceNotSupportedException(e);
        }
    }
}
