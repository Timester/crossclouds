package net.talqum.crossclouds.providers.aws.ec2;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.*;
import net.talqum.crossclouds.compute.Instance;
import net.talqum.crossclouds.compute.InstanceState;
import net.talqum.crossclouds.compute.common.AbstractComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.compute.node.Template;
import net.talqum.crossclouds.exceptions.ClientErrorCodes;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.exceptions.ProviderException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.*;

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
                .withKeyName(template.getImage().getCredentials())
                .withSecurityGroups(template.getOptions().getSecurityGroup());

        try {
            ((DefaultAWSEC2ComputeCloudContext) context).getClient().runInstances(runInstancesRequest);
        } catch (AmazonServiceException e) {
            throw handleAmazonServiceException(e);
        } catch (AmazonClientException e) {
            throw new ClientException(e, ClientErrorCodes.UNKNOWN);
        }
    }

    @Override
    public void startInstances(List<Instance> instances) {
        StartInstancesRequest req = new StartInstancesRequest();
        req.withInstanceIds(instances.stream().map(Instance::getId).collect(Collectors.toList()));

        try {
            ((DefaultAWSEC2ComputeCloudContext) context).getClient().startInstances(req);
        } catch (AmazonServiceException e) {
            throw handleAmazonServiceException(e);
        } catch (AmazonClientException e) {
            throw new ClientException(e, ClientErrorCodes.UNKNOWN);
        }
    }

    @Override
    public void stopInstances(List<Instance> instances) {
        StopInstancesRequest req = new StopInstancesRequest();
        req.withInstanceIds(instances.stream().map(Instance::getId).collect(Collectors.toList()));
        try {
            ((DefaultAWSEC2ComputeCloudContext) context).getClient().stopInstances(req);
        } catch (AmazonServiceException e) {
            throw handleAmazonServiceException(e);
        } catch (AmazonClientException e) {
            throw new ClientException(e, ClientErrorCodes.UNKNOWN);
        }
    }

    @Override
    public List<Instance> listInstances() {
        return listInstances(new ArrayList<>());
    }

    @Override
    public List<Instance> listInstances(List<String> instanceIDs) {
        DescribeInstancesRequest req = new DescribeInstancesRequest();
        req.withInstanceIds(instanceIDs);

        try {
            List<net.talqum.crossclouds.compute.Instance> returnList = new ArrayList<>();
            DescribeInstancesResult result = ((DefaultAWSEC2ComputeCloudContext) context).getClient().describeInstances(req);

            // TODO iter over result pages
            for (Reservation reservation : result.getReservations()) {
                for (com.amazonaws.services.ec2.model.Instance instance : reservation.getInstances()) {
                    InstanceState state;
                    switch (instance.getState().getName()) {
                        case "pending":
                            state = InstanceState.PROVISIONING;
                            break;
                        case "running":
                            state = InstanceState.RUNNING;
                            break;
                        case "shutting-down":
                            state = InstanceState.STOPPING;
                            break;
                        case "terminated":
                            state = InstanceState.TERMINATED;
                            break;
                        case "stopping":
                            state = InstanceState.STOPPING;
                            break;
                        case "stopped":
                            state = InstanceState.STOPPED;
                            break;
                        default:
                            state = InstanceState.UNKNOWN;
                    }

                    // TODO set zone
                    returnList.add(new Instance(instance.getInstanceId(), state));
                }
            }

            return returnList;

        } catch (AmazonServiceException e) {
            throw handleAmazonServiceException(e);
        } catch (AmazonClientException e) {
            throw new ClientException(e, ClientErrorCodes.UNKNOWN);
        }
    }

    private String checkTemplate(Template template) {
        List<String> errors = new ArrayList<>(7);

        if(template == null) { return "template"; }

        if(template.getHardware() == null) {
            errors.add("hardware");
        } else {
            if (isNullOrEmpty(template.getHardware().getConfigId())) {
                errors.add("hardware/configId");
            }
        }

        if(template.getImage() == null) {
            errors.add("image");
        } else {
            if (isNullOrEmpty(template.getImage().getOperatingSystem())) {
                errors.add("image/os");
            }
            if (isNullOrEmpty(template.getImage().getCredentials())) {
                errors.add("image/keypair");
            }
        }

        if(template.getOptions() == null) {
            errors.add("options");
        } else {
            if(isNullOrEmpty(template.getOptions().getSecurityGroup())) { errors.add("options/security group"); }
        }

        return errors.stream().collect(Collectors.joining(", "));
    }

    private ClientException handleAmazonServiceException(AmazonServiceException e) {
        if (e.getStatusCode() < 500) {
            if (e.getErrorCode().equals("InvalidKeyPair.NotFound")) {
                return new ClientException(e, ClientErrorCodes.NONEXISTENT_KEYPAIR);
            } else if (e.getErrorCode().equals("InvalidParameterValue")) {
                return new ClientException(e, ClientErrorCodes.INVALID_PARAMETER);
            } else {
                return new ClientException(e, ClientErrorCodes.UNKNOWN);
            }
        } else {
            return new ProviderException(e, ClientErrorCodes.UNKNOWN);
        }
    }
}
