package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.common.Context;
import net.talqum.crossclouds.providers.ContextConfig;
import net.talqum.crossclouds.providers.ContextFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 10. 05.
 * Time: 16:17
 */
public class LocationImpl implements Location {


    private final ContextConfig contextConfig;

    public LocationImpl(ContextConfig contextConfig) {

        this.contextConfig = contextConfig;
    }

    @Override
    public <C extends Context> C build() {
        return ContextFactory.build(contextConfig);
    }

    @Override
    public End location(String loc) {
        return new EndImpl(contextConfig.setLocation(loc));
    }
}
