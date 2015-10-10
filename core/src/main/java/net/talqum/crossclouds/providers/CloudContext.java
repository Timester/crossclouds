package net.talqum.crossclouds.providers;

import net.talqum.crossclouds.providers.factory.Provider;
import net.talqum.crossclouds.providers.factory.ProviderImpl;

public class CloudContext {
    public static Provider create(Class c) {
        return new ProviderImpl(new ContextConfig(c));
    }
}

