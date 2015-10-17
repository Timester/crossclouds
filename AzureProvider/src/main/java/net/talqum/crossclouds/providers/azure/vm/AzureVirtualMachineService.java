package net.talqum.crossclouds.providers.azure.vm;

import net.talqum.crossclouds.compute.Instance;
import net.talqum.crossclouds.compute.common.AbstractComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.compute.node.Template;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 08. 09.
 * Time: 18:10
 */
public class AzureVirtualMachineService extends AbstractComputeCloud {

    protected AzureVirtualMachineService(ComputeCloudContext context) {
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
