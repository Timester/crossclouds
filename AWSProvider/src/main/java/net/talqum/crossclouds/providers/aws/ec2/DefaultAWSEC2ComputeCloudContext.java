package net.talqum.crossclouds.providers.aws.ec2;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import net.talqum.crossclouds.compute.common.AbstractComputeCloudContext;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015.05.18.
 * Time: 15:04
 */
public class DefaultAWSEC2ComputeCloudContext extends AbstractComputeCloudContext implements AWSEC2ComputeCloudContext {

    private final AmazonEC2Client ec2Client;

    public DefaultAWSEC2ComputeCloudContext(String identity, String secret) {
        super();
        setComputeCloud(new AWSEC2ComputeCloud(this));

        ec2Client = new AmazonEC2Client(new BasicAWSCredentials(identity, secret));
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

    }
}
