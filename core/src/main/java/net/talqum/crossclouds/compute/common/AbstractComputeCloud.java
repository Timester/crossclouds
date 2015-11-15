package net.talqum.crossclouds.compute.common;

import net.talqum.crossclouds.compute.Instance;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractComputeCloud implements ComputeCloud {

    protected final ComputeCloudContext context;

    protected AbstractComputeCloud(ComputeCloudContext context) {
        this.context = context;
    }

    @Override
    public ComputeCloudContext getContext() {
        return context;
    }

    @Override
    public void startInstance(Instance instance) {
        startInstances(Collections.singletonList(instance));
    }

    @Override
    public void stopInstance(Instance instance) {
        stopInstances(Collections.singletonList(instance));
    }

    @Override
    public Instance getInstance(String instanceId) {
        List<Instance> instances = listInstances(Collections.singletonList(instanceId));

        if(instances.size() == 1) {
            return instances.get(0);
        } else {
            return null;
        }
    }
}
