package net.talqum.crossclouds.compute.common;

import net.talqum.crossclouds.compute.node.Template;

import java.util.List;
import java.util.Map;

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
    void stopInstance();
    Map<String, InstanceStatus> getInstanceStatus(List<String> instanceIds);
}
