package net.talqum.crossclouds.providers.azure.vm;

import net.talqum.crossclouds.compute.Instance;
import net.talqum.crossclouds.compute.common.AbstractComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.compute.common.InstanceStatus;
import net.talqum.crossclouds.compute.node.Template;

import java.util.List;
import java.util.Map;

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
    public void startInstance() {

    }

    @Override
    public void stopInstance(List<Instance> instanceIDs) {

    }

    @Override
    public List<Instance> listInstances() {
        return null;
    }

    @Override
    public Instance getInstance(String instanceId) {
        return null;
    }
}
