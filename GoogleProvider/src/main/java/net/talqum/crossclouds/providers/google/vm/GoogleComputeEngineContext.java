package net.talqum.crossclouds.providers.google.vm;

import net.talqum.crossclouds.compute.common.ComputeCloudContext;

public interface GoogleComputeEngineContext extends ComputeCloudContext {

    @Override
    GoogleComputeEngine getComputeCloud();
}
