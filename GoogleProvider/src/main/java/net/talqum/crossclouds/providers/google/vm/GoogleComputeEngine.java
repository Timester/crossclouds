package net.talqum.crossclouds.providers.google.vm;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.*;
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

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;

public class GoogleComputeEngine extends AbstractComputeCloud {

    final Logger log = LoggerFactory.getLogger(GoogleComputeEngine.class);

    private final Compute computeCloudClient;
    private final String projectId;
    private final String location;

    protected GoogleComputeEngine(ComputeCloudContext context) {
        super(context);
        this.computeCloudClient = ((DefaultGoogleComputeEngineContext) context).getClient();
        this.projectId = ((DefaultGoogleComputeEngineContext) context).getProjectId();
        this.location = ((DefaultGoogleComputeEngineContext) context).getLocation();
    }

    @Override
    public void createAndStartInstance(Template template) {
        String checkStatus = checkTemplate(template);

        if(!checkStatus.equals("")) {
            throw new IllegalArgumentException("Invalid template parameters: " + checkStatus);
        }

        // MACHINE TYPE
        com.google.api.services.compute.model.Instance instance = new com.google.api.services.compute.model.Instance();
        instance.setMachineType(DefaultGoogleComputeEngineContext.COMPUTE_API_URL + "/" +
                projectId + "/zones/" + template.getOptions().getLocation() + "/machineTypes/"
                + template.getHardware().getConfigId());

        // INSTANCE NAME
        instance.setName("cc-" + template.getName() + "-" + generateRandomString(10));

        // DISKS AND IMAGE
        AttachedDiskInitializeParams diskParams = new AttachedDiskInitializeParams();
        diskParams.setSourceImage(template.getImage().getOperatingSystem());

        AttachedDisk disk = new AttachedDisk();
        disk.setAutoDelete(true);
        disk.setBoot(true);
        disk.setType("PERSISTENT");
        disk.setInitializeParams(diskParams);

        instance.setDisks(Collections.singletonList(disk));

        // NETWORK
        List<NetworkInterface> networkInterfaces = new ArrayList<>();
        NetworkInterface iface = new NetworkInterface();
        iface.setFactory(DefaultGoogleComputeEngineContext.JSON_FACTORY);
        iface.setName("eth0");
        iface.setNetwork(DefaultGoogleComputeEngineContext.COMPUTE_API_URL + "/" + projectId + "/global/networks/default");
        networkInterfaces.add(iface);
        instance.setNetworkInterfaces(networkInterfaces);

        // ZONE
        instance.setZone(DefaultGoogleComputeEngineContext.COMPUTE_API_URL + "/" + projectId + "/zones/" + template.getOptions().getLocation());

         try {
             Compute.Instances.Insert insertRequest = computeCloudClient.instances().insert(projectId,
                    template.getOptions().getLocation(), instance);

             Operation op = insertRequest.execute();
        } catch (IOException e) {
             if(e instanceof GoogleJsonResponseException) {
                 if(((GoogleJsonResponseException)e).getStatusCode() == 400){
                     throw new ClientException(e, ClientErrorCodes.INVALID_PARAMETER);
                 }
             } else {
                 throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
             }
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
            Compute.Instances.List instanceListRequest =  computeCloudClient.instances().list(projectId, location);

            InstanceList instanceList;
            List<Instance> instances = new ArrayList<>();

            do {
                instanceList = instanceListRequest.execute();

                instances.addAll(instanceList.getItems().stream()
                        .map(instance -> new Instance(String.valueOf(instance.getId()), extractInstanceState(instance.getStatus()), location))
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
            Compute.Instances.List instanceListRequest =  computeCloudClient.instances().list(projectId, location);

            InstanceList instanceList;
            List<Instance> instances = new ArrayList<>();

            do {
                instanceList = instanceListRequest.execute();
                if(instanceList.getItems() != null) {
                    instances.addAll(instanceList.getItems().stream()
                            .filter(instance -> instanceIDs.contains(String.valueOf(instance.getId())))
                            .map(instance -> new Instance(String.valueOf(instance.getId()), extractInstanceState(instance.getStatus()), location))
                            .collect(Collectors.toList()));
                }

                instanceListRequest.setPageToken(instanceList.getNextPageToken());
            } while (null != instanceList.getNextPageToken());

            return instances;
        } catch (IOException e) {
            throw new ProviderException(e, ClientErrorCodes.IO_ERROR);
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
        }

        if(template.getOptions() == null) {
            errors.add("options");
        } else {
            if(isNullOrEmpty(template.getOptions().getLocation())) {
                log.warn("No Zone specified, trying default.");
                if(isNullOrEmpty(location)) {
                    log.error("No default zone specified.");
                    errors.add("location");
                }
            }
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
