package net.talqum.crossclouds.providers.aws.ec2;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.*;
import net.talqum.crossclouds.compute.Instance;
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
                .withKeyName(template.getImage().getCredentials())
                .withSecurityGroups(template.getOptions().getSecurityGroup());

        try {
            ((DefaultAWSEC2ComputeCloudContext) context).getClient().runInstances(runInstancesRequest);
        } catch (AmazonServiceException e) {
            if (e.getStatusCode() < 500) {
                if (e.getErrorCode().equals("InvalidAMIID.Malformed")) {
                    throw new ClientException(e, ClientErrorCodes.INVALID_PARAMETER);
                } else if (e.getErrorCode().equals("InvalidKeyPair.NotFound")) {
                    throw new ClientException(e, ClientErrorCodes.NONEXISTENT_KEYPAIR);
                } else if (e.getErrorCode().equals("InvalidParameterValue")) {
                    throw new ClientException(e, ClientErrorCodes.INVALID_PARAMETER);
                } else {
                    throw new ClientException(e, ClientErrorCodes.UNKNOWN);
                }
            } else {
                throw new ProviderException(e, ClientErrorCodes.UNKNOWN);
            }
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
    public void stopInstance(List<Instance> instances) {
        StopInstancesRequest req = new StopInstancesRequest();
        req.withInstanceIds(instances.stream().map(x -> x.getId()).collect(Collectors.toList()));
        try {
            StopInstancesResult stopInstancesResult = ((DefaultAWSEC2ComputeCloudContext) context).getClient().stopInstances(req);
        } catch (AmazonServiceException e) {
            if (e.getStatusCode() < 500) {
                // TODO
                if (e.getErrorCode().equals("IncorrectInstanceState")) {
                    throw new ClientException(e, ClientErrorCodes.INVALID_PARAMETER);
                } else if (e.getErrorCode().equals("InvalidKeyPair.NotFound")) {
                    throw new ClientException(e, ClientErrorCodes.NONEXISTENT_KEYPAIR);
                } else if (e.getErrorCode().equals("InvalidParameterValue")) {
                    throw new ClientException(e, ClientErrorCodes.INVALID_PARAMETER);
                } else {
                    throw new ClientException(e, ClientErrorCodes.UNKNOWN);
                }
            } else {
                throw new ProviderException(e, ClientErrorCodes.UNKNOWN);
            }
        } catch (AmazonClientException e) {
            throw new ClientException(e, ClientErrorCodes.UNKNOWN);
        }
    }

    @Override
    public List<Instance> listInstances() {
        DescribeInstancesRequest req = new DescribeInstancesRequest();

        DescribeInstancesResult res = ((DefaultAWSEC2ComputeCloudContext) context).getClient().describeInstances(req);

        // TODO

        return null;
    }

    @Override
    public Instance getInstance(String instanceId) {
        return null;
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
}
