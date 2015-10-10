package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.common.Context;
import net.talqum.crossclouds.providers.ContextConfig;
import net.talqum.crossclouds.providers.ContextFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 10. 05.
 * Time: 16:16
 */
public class SecretImpl implements Secret {
    private final ContextConfig contextConfig;

    public SecretImpl(ContextConfig contextConfig) {

        this.contextConfig = contextConfig;
    }

    @Override
    public Async secret(String secret) {
        return new AsyncImpl(contextConfig.setSecret(secret));
    }

    @Override
    public <C extends Context> C build() {
        return ContextFactory.build(contextConfig);
    }
}
