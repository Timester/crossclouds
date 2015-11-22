package net.talqum.crossclouds.compute;

import java.util.Objects;

/**
 * Describes a virtual machine in the cloud.
 */
public class Instance {
    private final String id;
    private final InstanceState state;

    private final String zone;
    private final String imageId;
    private final String hwConfigId;


    private Instance(Builder b) {
        this.id = b.id;
        this.state = b.state;
        this.zone = b.zone;
        this.imageId = b.imageId;
        this.hwConfigId = b.hwConfigId;
    }

    public static class Builder {
        private final String id;
        private InstanceState state;

        private String zone;
        private String imageId;
        private String hwConfigId;

        public Builder(String instanceId){
            this.id = instanceId;
        }

        public Builder state(InstanceState state) {
            this.state = state;
            return this;
        }

        public Builder zone(String zone) {
            this.zone = zone;
            return this;
        }

        public Builder imageId(String imageId) {
            this.imageId = imageId;
            return this;
        }

        public Builder hwConfig(String hwConfigId) {
            this.hwConfigId = hwConfigId;
            return this;
        }

        public Instance build() {
            return new Instance(this);
        }
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

    public String getImageId() {
        return imageId;
    }

    public String getHwConfigId() {
        return hwConfigId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instance instance = (Instance) o;
        return Objects.equals(id, instance.id) &&
                state == instance.state &&
                Objects.equals(zone, instance.zone) &&
                Objects.equals(imageId, instance.imageId) &&
                Objects.equals(hwConfigId, instance.hwConfigId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, zone, imageId, hwConfigId);
    }
}
