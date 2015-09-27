package net.talqum.crossclouds.compute.common;

import net.talqum.crossclouds.compute.node.Template;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015.05.17.
 * Time: 11:24
 */
public interface ComputeCloud {
    ComputeCloudContext getContext();

    void createInstance(Template template);
}
