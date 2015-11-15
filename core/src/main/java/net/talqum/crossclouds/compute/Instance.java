package net.talqum.crossclouds.compute;

/**
 * Describes a virtual machine in the cloud.
 */
public class Instance {
    private final String id;
    private final InstanceState state;

    private String zone;


    public Instance(String id, InstanceState state) {
        this.id = id;
        this.state = state;
    }

    public Instance(String id, InstanceState state, String zone) {
        this.id = id;
        this.state = state;
        this.zone = zone;
    }

    public String getId() {
        return id;
    }

    public InstanceState getState() {
        return state;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }
}
