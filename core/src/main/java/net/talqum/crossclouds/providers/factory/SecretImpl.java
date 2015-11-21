package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.common.Context;
import net.talqum.crossclouds.providers.ContextConfig;
import net.talqum.crossclouds.providers.ContextFactory;

public class SecretImpl implements Secret {
    private final ContextConfig contextConfig;

    public SecretImpl(ContextConfig contextConfig) {

        this.contextConfig = contextConfig;
    }

    @Override
    public Location secret(String secret) {
        return new LocationImpl(contextConfig.setSecret(secret));
    }

    @Override
    public <C extends Context> C build() {
        return ContextFactory.build(contextConfig);
    }
}
