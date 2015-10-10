package net.talqum.crossclouds.providers;

import com.google.common.reflect.TypeToken;
import net.talqum.crossclouds.common.Context;
import net.talqum.crossclouds.common.ProviderMetadata;
import net.talqum.crossclouds.exceptions.ServiceNotSupportedException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static com.google.common.base.Strings.*;
import static net.talqum.crossclouds.util.reflect.TypeConverter.typeToken;

/**
 * Created by Imre on 2015.03.07..
 */
public class ContextFactory {

    private static ProviderMetadata getProviderMetadata(String provider) {
        return Providers.find(provider);
    }

    @SuppressWarnings("unchecked")
    public static <C extends Context> C build(ContextConfig config) {

        if (!validateConfig(config)) {
            throw new IllegalArgumentException("Invalid configuration.");
        }

        TypeToken<C> cTypeToken = typeToken(config.getContextType());

        try {
            List<TypeToken<? extends Context>> services = getProviderMetadata(config.getProviderId()).getServices();
            Class<?> rawType = null;

            for (TypeToken<? extends Context> serviceTypeToken : services) {
                if(cTypeToken.isAssignableFrom(serviceTypeToken)){
                    rawType = serviceTypeToken.getRawType();
                }
            }

            if (rawType != null) {
                switch (config.getCredentialType()) {
                    case ID_SECRET:
                        return (C) rawType.getConstructor(String.class, String.class)
                                .newInstance(config.getId(), config.getSecret());
                    case KEY:
                        return (C) rawType.getConstructor(String.class, String.class, String.class)
                                .newInstance(config.getAppName(), config.getAccId(), config.getKeyPath());
                    default:
                        throw new IllegalArgumentException("Invalid credential type.");
                }
            } else {
                throw new ServiceNotSupportedException("No service found for the given criteria.");
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new ServiceNotSupportedException(e);
        }
    }

    private static boolean validateConfig(ContextConfig config) {
        if (!isNullOrEmpty(config.getProviderId()) && (config.getContextType() != null)) {
            if (config.getCredentialType().equals(CredentialType.KEY)){
                return validateKeyBasedConfig(config);
            } else {
                return validateIDSecBasedConfig(config);
            }
        } else {
            return false;
        }
    }

    private static boolean validateIDSecBasedConfig(ContextConfig conf) {
        return !isNullOrEmpty(conf.getId()) && !isNullOrEmpty(conf.getSecret());
    }

    private static boolean validateKeyBasedConfig(ContextConfig conf) {
        return !isNullOrEmpty(conf.getKeyPath()) && !isNullOrEmpty(conf.getAccId()) &&
                !isNullOrEmpty(conf.getAppName());
    }
}
