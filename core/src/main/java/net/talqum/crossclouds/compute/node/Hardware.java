package net.talqum.crossclouds.compute.node;

public class Hardware {
    private final String configId;

    private Hardware(Builder builder) {
        this.configId = builder.configId;
    }

    public static class Builder {
        private final String configId;

        public Builder(String configId) {
            this.configId = configId;
        }

        public Hardware build() {
            return new Hardware(this);
        }
    }

    public String getConfigId() {
        return this.configId;
    }
}
