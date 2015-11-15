package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.providers.ContextConfig;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 10. 05.
 * Time: 16:16
 */
public class ProjectIdImpl implements ProjectId {
    private final ContextConfig contextConfig;

    public ProjectIdImpl(ContextConfig contextConfig) {
        this.contextConfig = contextConfig;
    }

    @Override
    public KeyPath projectId(String appName) {
        return new KeyPathImpl(contextConfig.setProjectId(appName));
    }
}
