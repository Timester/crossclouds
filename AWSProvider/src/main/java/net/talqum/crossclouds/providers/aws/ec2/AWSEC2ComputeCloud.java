package net.talqum.crossclouds.providers.aws.ec2;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import net.talqum.crossclouds.compute.Instance;
import net.talqum.crossclouds.compute.InstanceState;
import net.talqum.crossclouds.compute.common.AbstractComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.compute.node.Template;
import net.talqum.crossclouds.exceptions.ClientErrorCodes;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.exceptions.ProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.*;

public class AWSEC2ComputeCloud extends AbstractComputeCloud {

    final Logger log = LoggerFactory.getLogger(AWSEC2ComputeCloud.class);
    
    private final AmazonEC2Client client;
    private final String location;

    protected AWSEC2ComputeCloud(DefaultAWSEC2ComputeCloudContext context) {
        super(context);

        this.client = context.getClient();
        this.location = context.getLocation();
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

        if(!isNullOrEmpty(template.getOptions().getLocation())) {
            runInstancesRequest.withPlacement(new Placement().withAvailabilityZone(template.getOptions().getLocation()));
        } else if(!isNullOrEmpty(location)) {
            runInstancesRequest.withPlacement(new Placement().withAvailabilityZone(location));
        }

        try {
            client.runInstances(runInstancesRequest);
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
            client.startInstances(req);
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
            client.stopInstances(req);
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
            DescribeInstancesResult result = client.describeInstances(req);

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

                    Instance inst = new Instance.Builder(instance.getInstanceId())
                            .state(state)
                            .hwConfig(instance.getInstanceType())
                            .imageId(instance.getImageId())
                            .zone(instance.getPlacement().getAvailabilityZone())
                            .build();

                    returnList.add(inst);
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
            switch (e.getErrorCode()) {
                case "InvalidKeyPair.NotFound":
                    return new ClientException(e, ClientErrorCodes.NONEXISTENT_KEYPAIR);
                case "InvalidInstanceID.NotFound":
                    return new ClientException(e, ClientErrorCodes.INSTANCE_NOT_FOUND);
                case "InvalidParameterValue":
                    return new ClientException(e, ClientErrorCodes.INVALID_PARAMETER);
                default:
                    return new ClientException(e, ClientErrorCodes.UNKNOWN);
            }
        } else {
            return new ProviderException(e, ClientErrorCodes.UNKNOWN);
        }
    }
}
