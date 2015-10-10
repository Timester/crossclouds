package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.providers.ContextConfig;

/**
 * Created by imre on 10/10/15.
 */
public class ProviderImpl implements Provider {
    private final ContextConfig contextConfig;

    public ProviderImpl(ContextConfig contextConfig) {
        this.contextConfig = contextConfig;
    }

    @Override
    public Credentials fromProvider(String providerId) {
        contextConfig.setProviderId(providerId);
        return new CredentialsImpl(contextConfig);
    }
}
