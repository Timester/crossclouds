package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.providers.ContextConfig;

public class CredentialsImpl implements Credentials {

    private final ContextConfig contextConfig;

    public CredentialsImpl(ContextConfig contextConfig) {
        this.contextConfig = contextConfig;
    }

    @Override
    public CredentialsSetup addCredentials() {
        return new CredentialsSetupImpl(contextConfig);
    }
}
