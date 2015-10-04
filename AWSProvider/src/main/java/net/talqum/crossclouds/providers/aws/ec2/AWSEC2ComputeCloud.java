package net.talqum.crossclouds.providers.aws.ec2;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.*;
import com.google.common.base.Strings;
import net.talqum.crossclouds.compute.common.AbstractComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.compute.common.InstanceStatus;
import net.talqum.crossclouds.compute.node.Template;
import net.talqum.crossclouds.exceptions.ClientErrorCodes;
import net.talqum.crossclouds.exceptions.ClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public void createAndStartInstance(Template template) {

        String checkStatus = checkTemplate(template);

        if(!checkStatus.equals("")) {
            throw new IllegalArgumentException("Invalid template parameters: " + checkStatus);
        }

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

        runInstancesRequest.withImageId(template.getImage().getOperatingSystem())
                .withInstanceType(template.getHardware().getConfigId())
                .withMinCount(template.getOptions().getMinInstanceCount())
                .withMaxCount(template.getOptions().getMaxInstanceCount())
                .withKeyName(template.getImage().getDefaultCredentials())
                .withSecurityGroups(template.getOptions().getSecurityGroup());

        try {
            ((DefaultAWSEC2ComputeCloudContext) context).getClient().runInstances(runInstancesRequest);
        } catch (AmazonServiceException e) {
            // TODO check which params were incorect and add message
            throw new IllegalArgumentException("Invalid parameters: ");
        } catch (AmazonClientException e) {
            throw new ClientException(e, ClientErrorCodes.UNKNOWN);
        }
    }

    @Override
    public void startInstance() {
        // TODO
        StartInstancesRequest req = null;
        StartInstancesResult startInstancesResult = ((DefaultAWSEC2ComputeCloudContext) context).getClient().startInstances(req);
    }

    @Override
    public void stopInstance() {
        // TODO
        StopInstancesRequest req = null;
        StopInstancesResult stopInstancesResult = ((DefaultAWSEC2ComputeCloudContext) context).getClient().stopInstances(req);
    }

    @Override
    public Map<String,InstanceStatus> getInstanceStatus(List<String> instanceIds) {
        // TODO
        DescribeInstancesRequest req = null;
        DescribeInstancesResult describeInstancesResult = ((DefaultAWSEC2ComputeCloudContext) context).getClient().describeInstances(req);
        return null;
    }

    private String checkTemplate(Template template) {
        List<String> errors = new ArrayList<>(7);

        if(template == null) { errors.add("template"); }
        if(template.getImage() == null) { errors.add("image"); }
        if(template.getOptions() == null) { errors.add("options"); }
        if(template.getHardware() == null) { errors.add("hardware"); }
        if(Strings.isNullOrEmpty(template.getImage().getOperatingSystem())) { errors.add("operating system"); }
        if(Strings.isNullOrEmpty(template.getImage().getDefaultCredentials())) { errors.add("keypair"); }
        if(Strings.isNullOrEmpty(template.getOptions().getSecurityGroup())) { errors.add("security group"); }

        return errors.stream().collect(Collectors.joining(", "));
    }
}
