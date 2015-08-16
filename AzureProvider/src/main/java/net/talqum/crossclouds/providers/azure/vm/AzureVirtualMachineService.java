package net.talqum.crossclouds.providers.azure.vm;

import net.talqum.crossclouds.compute.common.AbstractComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.compute.vm.CreateRequest;

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
    public void createInstance(CreateRequest cr) {

    }
}
