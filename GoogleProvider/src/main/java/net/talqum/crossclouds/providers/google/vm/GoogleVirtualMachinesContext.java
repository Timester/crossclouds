package net.talqum.crossclouds.providers.google.vm;

import net.talqum.crossclouds.compute.common.ComputeCloudContext;

public interface GoogleVirtualMachinesContext extends ComputeCloudContext {

    @Override
    GoogleVirtualMachineService getComputeCloud();
}
