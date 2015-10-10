package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.providers.ContextConfig;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 10. 05.
 * Time: 16:50
 */
public class KeyPathImpl implements KeyPath {
    private final ContextConfig contextConfig;

    public KeyPathImpl(ContextConfig contextConfig) {

        this.contextConfig = contextConfig;
    }

    @Override
    public Async keyPath(String path) {
        return new AsyncImpl(contextConfig.setKeyPath(path));
    }
}
