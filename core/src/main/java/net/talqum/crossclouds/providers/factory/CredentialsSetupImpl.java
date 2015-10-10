package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.providers.ContextConfig;
import net.talqum.crossclouds.providers.CredentialType;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 10. 05.
 * Time: 16:08
 */
public class CredentialsSetupImpl implements CredentialsSetup {
    private final ContextConfig contextConfig;

    public CredentialsSetupImpl(ContextConfig contextConfig) {

        this.contextConfig = contextConfig;
    }

    @Override
    public KeyCred keyBased() {
        this.contextConfig.setCredentialType(CredentialType.KEY);
        return new KeyCredImpl(contextConfig);
    }

    @Override
    public IdSecCred idAndSecretBased() {
        this.contextConfig.setCredentialType(CredentialType.ID_SECRET);
        return new IdSecCredImpl(contextConfig);
    }
}
