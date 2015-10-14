package net.talqum.crossclouds.compute;

/**
 * Created by imre on 10/14/15.
 *
 * Describes a virtual machine in the cloud.
 */
public class Instance {
    private final String id;
    private final InstanceState state;

    public Instance(String id, InstanceState state) {
        this.id = id;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public InstanceState getState() {
        return state;
    }
}
