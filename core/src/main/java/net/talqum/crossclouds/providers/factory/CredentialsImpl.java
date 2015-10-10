package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.providers.ContextConfig;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 10. 04.
 * Time: 17:56
 */
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
