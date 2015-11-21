package net.talqum.crossclouds.compute.common;

import net.talqum.crossclouds.compute.Instance;
import net.talqum.crossclouds.compute.node.Template;

import java.util.List;

public interface ComputeCloud {
    ComputeCloudContext getContext();

    /**
     * Creates and starts a new virtual machine instance from the details provided in the template parameter.
     *
     * @param template the details for the virtual machine to be created. Providers may require different paramteres to
     *                 be set. Each provider should check this object if all needed parameters are set. In case of a missing
     *                 on an IllegalArgumentException should be thrown. In case of an invalid one a ClientException is thrown.
     *
     * @exception IllegalArgumentException if a required parameter is not set
     * @exception net.talqum.crossclouds.exceptions.ClientException if a parameter is not valid
     */
    void createAndStartInstance(Template template);

    /**
     * Starts an already existing and currently stopped instance. Instance is identified by some kind of identifier
     * provided in the Instance object.
     *
     * @param instance an Instance object identifying the virtual machine to be started
     *
     * @exception net.talqum.crossclouds.exceptions.ClientException
     */
    void startInstance(Instance instance);

    /**
     * Stops an already existing and currently running instance. Instance is identified by some kind of identifier
     * provided in the Instance object.
     *
     * @param instance an Instance object identifying the virtual machine to be stopped
     *
     * @exception net.talqum.crossclouds.exceptions.ClientException
     */
    void stopInstance(Instance instance);


    /**
     * Starts multiple already existing and currently stopped instances. Instances are identified by some kind of identifier
     * provided in the Instance objects.
     *
     * @param instances list of Instance identifiers
     *
     * @exception net.talqum.crossclouds.exceptions.ClientException
     */
    void startInstances(List<Instance> instances);

    /**
     * Stops multiple already existing and currently running instances. Instances are identified by some kind of identifier
     * provided in the Instance objects.
     *
     * @param instances list of Instance identifiers
     *
     * @exception net.talqum.crossclouds.exceptions.ClientException
     */
    void stopInstances(List<Instance> instances);

    /**
     * Lists all instances owned by the user. Returns a list of instance metadata.
     *
     * @return list of Instance objects containing instance metadata, empty list if none was found
     *
     * @exception net.talqum.crossclouds.exceptions.ClientException
     */
    List<Instance> listInstances();

    /**
     * Returns metadata for the virtual machines identified by the given instance id list
     *
     * @param instanceIDs a list of virtual machine ids
     * @return list of Instance objects containing instance metadata, empty list if none was found
     *
     * @exception net.talqum.crossclouds.exceptions.ClientException
     */
    List<Instance> listInstances(List<String> instanceIDs);

    /**
     * Returns metadata for the virtual machine identified by the given instance id
     *
     * @param instanceId identifier for a virtual machine
     * @return an Instance object containing instance metadata, or null if virtual machine was not found
     *
     * @exception net.talqum.crossclouds.exceptions.ClientException
     */
    Instance getInstance(String instanceId);
}
