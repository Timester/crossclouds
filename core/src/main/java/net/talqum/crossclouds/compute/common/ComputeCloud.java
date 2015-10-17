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

    void startInstance(Instance instance);
    void stopInstance(Instance instance);
    void startInstances(List<Instance> instances);
    void stopInstances(List<Instance> instances);

    List<Instance> listInstances();
    List<Instance> listInstances(List<String> instanceIDs);
    Instance getInstance(String instanceId);
}
