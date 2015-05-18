package net.talqum.crossclouds.providers.aws.ec2;

import net.talqum.crossclouds.compute.common.AbstractComputeCloudContext;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015.05.18.
 * Time: 15:04
 */
public class DefaultAWSEC2ComputeCloudContext extends AbstractComputeCloudContext implements AWSEC2ComputeCloudContext {
    @Override
    public void close() throws IOException {

    }
}
