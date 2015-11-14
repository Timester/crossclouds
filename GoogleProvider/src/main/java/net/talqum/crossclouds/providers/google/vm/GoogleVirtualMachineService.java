package net.talqum.crossclouds.providers.google.vm;

import net.talqum.crossclouds.compute.Instance;
import net.talqum.crossclouds.compute.common.AbstractComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.compute.node.Template;

import java.util.List;

public class GoogleVirtualMachineService extends AbstractComputeCloud {

    protected GoogleVirtualMachineService(ComputeCloudContext context) {
        super(context);
    }

    @Override
    public void createAndStartInstance(Template template) {

    }

    @Override
    public void startInstances(List<Instance> instances) {

    }

    @Override
    public void stopInstances(List<Instance> instances) {

    }

    @Override
    public List<Instance> listInstances() {
        return null;
    }

    @Override
    public List<Instance> listInstances(List<String> instanceIDs) {
        return null;
    }
}
