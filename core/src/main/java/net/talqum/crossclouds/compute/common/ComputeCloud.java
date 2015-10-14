package net.talqum.crossclouds.compute.common;

import net.talqum.crossclouds.compute.Instance;
import net.talqum.crossclouds.compute.node.Template;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015.05.17.
 * Time: 11:24
 */
public interface ComputeCloud {
    ComputeCloudContext getContext();

    void createAndStartInstance(Template template);
    void startInstance();
    void stopInstance(List<Instance> instanceIDs);
    List<Instance> listInstances();
    Instance getInstance(String instanceId);
}
