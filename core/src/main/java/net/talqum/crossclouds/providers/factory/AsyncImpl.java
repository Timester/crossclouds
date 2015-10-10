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
public class AsyncImpl implements Async {
    private final ContextConfig contextConfig;

    public AsyncImpl(ContextConfig contextConfig) {
        this.contextConfig = contextConfig;
    }

    @Override
    public Location async(boolean async) {
        return new LocationImpl(contextConfig.setIsAsync(async));
    }

    @Override
    public <C extends Context> C build() {
        return ContextFactory.build(contextConfig);
    }
}
