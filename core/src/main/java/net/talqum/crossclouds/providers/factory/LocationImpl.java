package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.common.Context;
import net.talqum.crossclouds.providers.ContextConfig;
import net.talqum.crossclouds.providers.ContextFactory;

public class LocationImpl implements Location {


    private final ContextConfig contextConfig;

    public LocationImpl(ContextConfig contextConfig) {

        this.contextConfig = contextConfig;
    }

    @Override
    public Async location(String loc) {
        return new AsyncImpl(contextConfig.setLocation(loc));
    }

    @Override
    public <C extends Context> C build() {
        return ContextFactory.build(contextConfig);
    }
}
