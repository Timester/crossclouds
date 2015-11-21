package net.talqum.crossclouds.compute.common;

import net.talqum.crossclouds.common.Context;

public interface ComputeCloudContext extends Context {
    ComputeCloud getComputeCloud();
}
