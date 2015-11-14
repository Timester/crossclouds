package net.talqum.crossclouds.providers.aws.ec2;

import net.talqum.crossclouds.compute.common.ComputeCloudContext;

public interface AWSEC2ComputeCloudContext extends ComputeCloudContext {

    @Override
    AWSEC2ComputeCloud getComputeCloud();
}
