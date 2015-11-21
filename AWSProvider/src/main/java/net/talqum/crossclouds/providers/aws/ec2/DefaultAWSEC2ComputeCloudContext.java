package net.talqum.crossclouds.providers.aws.ec2;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import net.talqum.crossclouds.compute.common.AbstractComputeCloudContext;
import net.talqum.crossclouds.providers.ContextConfig;

import java.io.IOException;

public class DefaultAWSEC2ComputeCloudContext extends AbstractComputeCloudContext implements AWSEC2ComputeCloudContext {

    private final AmazonEC2Client ec2Client;

    public DefaultAWSEC2ComputeCloudContext(ContextConfig cfg) {
        super();
        setComputeCloud(new AWSEC2ComputeCloud(this));

        ec2Client = new AmazonEC2Client(new BasicAWSCredentials(cfg.getId(), cfg.getSecret()));

        this.async = cfg.isAsync();
        this.location = cfg.getLocation();
    }

    @Override
    public AWSEC2ComputeCloud getComputeCloud() {
        return AWSEC2ComputeCloud.class.cast(super.getComputeCloud());
    }

    public AmazonEC2Client getClient() {
        return ec2Client;
    }

    @Override
    public void close() throws IOException {
        ec2Client.shutdown();
    }
}
