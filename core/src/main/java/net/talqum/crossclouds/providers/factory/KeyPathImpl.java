package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.providers.ContextConfig;

public class KeyPathImpl implements KeyPath {
    private final ContextConfig contextConfig;

    public KeyPathImpl(ContextConfig contextConfig) {

        this.contextConfig = contextConfig;
    }

    @Override
    public LocationImpl keyPath(String path) {
        return new LocationImpl(contextConfig.setKeyPath(path));
    }
}
