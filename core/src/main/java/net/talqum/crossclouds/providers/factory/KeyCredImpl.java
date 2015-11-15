package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.providers.ContextConfig;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 10. 05.
 * Time: 16:08
 */
public class KeyCredImpl implements KeyCred {
    private final ContextConfig contextConfig;

    public KeyCredImpl(ContextConfig contextConfig) {

        this.contextConfig = contextConfig;
    }

    @Override
    public ProjectId accountId(String accId) {
        return new ProjectIdImpl(contextConfig.setAccId(accId));
    }
}
