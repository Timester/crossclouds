package net.talqum.crossclouds.providers.aws.ec2;

import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import net.talqum.crossclouds.compute.common.AbstractComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.compute.node.Template;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015.05.19.
 * Time: 18:24
 */
public class AWSEC2ComputeCloud extends AbstractComputeCloud {

    protected AWSEC2ComputeCloud(ComputeCloudContext context) {
        super(context);
    }

    @Override
    public void createInstance(Template template) {
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

        // TODO finish
        runInstancesRequest.withImageId(template.getImage().getOperatingSystem())
                .withInstanceType(template.getHardware().getConfigId())
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName(template.getImage().getDefaultCredentials())
                .withSecurityGroups(template.getOptions().getSecurityGroup());

        RunInstancesResult runInstancesResult =
                ((DefaultAWSEC2ComputeCloudContext)context).getClient().runInstances(runInstancesRequest);

    }

    private boolean checkTemplate(Template template) {
        // TODO
        return true;
    }
}
