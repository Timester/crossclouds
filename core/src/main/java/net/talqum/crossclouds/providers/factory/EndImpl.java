package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.common.Context;
import net.talqum.crossclouds.providers.ContextConfig;
import net.talqum.crossclouds.providers.ContextFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 10. 05.
 * Time: 16:18
 */
public class EndImpl implements End {
    private final ContextConfig contextConfig;

    public EndImpl(ContextConfig contextConfig) {

        this.contextConfig = contextConfig;
    }

    @Override
    public <C extends Context> C build() {
        return ContextFactory.build(contextConfig);
    }
}
