package net.talqum.crossclouds.providers.google.vm;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.InstanceList;
import com.google.api.services.compute.model.NetworkInterface;
import com.google.api.services.compute.model.Operation;
import net.talqum.crossclouds.compute.Instance;
import net.talqum.crossclouds.compute.InstanceState;
import net.talqum.crossclouds.compute.common.AbstractComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.compute.node.Template;
import net.talqum.crossclouds.exceptions.ClientErrorCodes;
import net.talqum.crossclouds.exceptions.ProviderException;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;

public class GoogleComputeEngine extends AbstractComputeCloud {

    private final Compute computeCloudClient;
    private final String projectId;

    protected GoogleComputeEngine(ComputeCloudContext context) {
        super(context);
        this.computeCloudClient = ((DefaultGoogleComputeEngineContext) context).getClient();
        this.projectId = ((DefaultGoogleComputeEngineContext) context).getProjectId();
    }

    @Override
    public void createAndStartInstance(Template template) {
        String checkStatus = checkTemplate(template);

        if(!checkStatus.equals("")) {
            throw new IllegalArgumentException("Invalid template parameters: " + checkStatus);
        }


        com.google.api.services.compute.model.Instance instance = new com.google.api.services.compute.model.Instance();
        instance.setMachineType(DefaultGoogleComputeEngineContext.COMPUTE_API_URL +
                projectId + "/zones/" + template.getOptions().getLocation() + "machineTypes/"
                + template.getHardware().getConfigId());

        instance.setName(template.getName() + "_" + generateRandomString(10));

        List<NetworkInterface> networkInterfaces = new ArrayList<>();
        NetworkInterface iface = new NetworkInterface();
        iface.setFactory(DefaultGoogleComputeEngineContext.JSON_FACTORY);
        iface.setName("eth0");
        iface.setNetwork(DefaultGoogleComputeEngineContext.COMPUTE_API_URL + "/" + projectId + "/global/networks/default");
        networkInterfaces.add(iface);
        instance.setNetworkInterfaces(networkInterfaces);

        instance.setZone(DefaultGoogleComputeEngineContext.COMPUTE_API_URL + "/" + projectId + "/zones/" + template.getOptions().getLocation());

         try {
             Compute.Instances.Insert insertRequest = computeCloudClient.instances().insert(projectId,
                    template.getOptions().getLocation(), instance);

             Operation op = insertRequest.execute();
        } catch (IOException e) {
             throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
        }

    }

    @Override
    public void startInstances(List<Instance> instances) {
        try {
            for (Instance instance : instances) {
                Compute.Instances.Start startInsanceRequest = computeCloudClient.instances()
                        .start(projectId, instance.getZone(), instance.getId());

                startInsanceRequest.execute();
            }
        } catch (IOException e) {
            throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
        }
    }

    @Override
    public void stopInstances(List<Instance> instances) {
        try {
            for (Instance instance : instances) {
                Compute.Instances.Stop stopInstanceRequest = computeCloudClient.instances()
                        .stop(projectId, instance.getZone(), instance.getId());

                stopInstanceRequest.execute();
            }
        } catch (IOException e) {
            throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
        }
    }

    @Override
    public List<Instance> listInstances() {
        try {
            // TODO zone, project id?
            Compute.Instances.List instanceListRequest =  computeCloudClient.instances().list(projectId, "");

            InstanceList instanceList;
            List<Instance> instances = new ArrayList<>();

            do {
                instanceList = instanceListRequest.execute();

                instances.addAll(instanceList.getItems().stream()
                        .map(instance -> new Instance(String.valueOf(instance.getId()), extractInstanceState(instance.getStatus()), ""))
                        .collect(Collectors.toList()));

                instanceListRequest.setPageToken(instanceList.getNextPageToken());
            } while (null != instanceList.getNextPageToken());

            return instances;
        } catch (IOException e) {
            throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
        }
    }

    @Override
    public List<Instance> listInstances(List<String> instanceIDs) {
        try {
            // TODO zone, project id?
            Compute.Instances.List instanceListRequest =  computeCloudClient.instances().list(projectId, "");

            InstanceList instanceList;
            List<Instance> instances = new ArrayList<>();

            do {
                instanceList = instanceListRequest.execute();
                if(instanceList.getItems() != null) {
                    instances.addAll(instanceList.getItems().stream()
                            .filter(instance -> instanceIDs.contains(String.valueOf(instance.getId())))
                            .map(instance -> new Instance(String.valueOf(instance.getId()), extractInstanceState(instance.getStatus()), ""))
                            .collect(Collectors.toList()));
                }

                instanceListRequest.setPageToken(instanceList.getNextPageToken());
            } while (null != instanceList.getNextPageToken());

            return instances;
        } catch (IOException e) {
            throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
        }
    }

    // TODO
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

    private InstanceState extractInstanceState(String status) {
        InstanceState state;
        switch (status) {
            case "PROVISIONING":
                state = InstanceState.PROVISIONING;
                break;
            case "STAGING":
                state = InstanceState.STARTING;
                break;
            case "RUNNING":
                state = InstanceState.RUNNING;
                break;
            case "STOPPING":
                state = InstanceState.STOPPING;
                break;
            case "TERMINATED":
                state = InstanceState.STOPPED;
                break;
            default:
                state = InstanceState.UNKNOWN;
        }

        return state;
    }

    private String generateRandomString(int length) {
        String rand = new BigInteger(130, new SecureRandom()).toString(32);

        return rand.substring(0, length);
    }
}
