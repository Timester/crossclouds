package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.providers.ContextConfig;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 10. 05.
 * Time: 16:08
 */
public class IdSecCredImpl implements IdSecCred {
    private final ContextConfig contextConfig;

    public IdSecCredImpl(ContextConfig contextConfig) {

        this.contextConfig = contextConfig;
    }

    @Override
    public Secret id(String id) {
        return new SecretImpl(contextConfig.setId(id));
    }
}
