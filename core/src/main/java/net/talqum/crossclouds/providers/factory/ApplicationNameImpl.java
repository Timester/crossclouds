package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.providers.ContextConfig;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 10. 05.
 * Time: 16:16
 */
public class ApplicationNameImpl implements ApplicationName {
    private final ContextConfig contextConfig;

    public ApplicationNameImpl(ContextConfig contextConfig) {
        this.contextConfig = contextConfig;
    }

    @Override
    public KeyPath applicationName(String appName) {
        return new KeyPathImpl(contextConfig.setAppName(appName));
    }
}
