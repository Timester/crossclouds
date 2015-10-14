package net.talqum.crossclouds.compute.node;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 10. 03.
 * Time: 16:40
 */
public class Options {
    public static final int DEFAULT_MIN_INSTANCES_COUNT = 1;
    public static final int DEFAULT_MAX_INSTANCES_COUNT = 1;

    private final String securityGroup;
    private final String networkSettings;
    private final int minInstanceCount;
    private final int maxInstanceCount;

    private Options(Builder builder) {
        this.securityGroup = builder.securityGroup;
        this.networkSettings = builder.networkSettings;
        this.minInstanceCount = builder.minInstanceCount;
        this.maxInstanceCount = builder.maxInstanceCount;
    }

    public static class Builder {
        private String securityGroup;
        private String networkSettings;
        private int minInstanceCount = DEFAULT_MIN_INSTANCES_COUNT;
        private int maxInstanceCount = DEFAULT_MAX_INSTANCES_COUNT;

        public Builder securityGroup(String securityGroup) {
            this.securityGroup = securityGroup;
            return this;
        }

        public Builder networkSettings(String networkSettings) {
            this.networkSettings = networkSettings;
            return this;
        }

        public Builder minInstancesCount(int count) {
            this.minInstanceCount = count;
            return this;
        }

        public Builder maxInstancesCount(int count) {
            this.maxInstanceCount = count;
            return this;
        }

        public Options build() { return new Options(this); }
    }

    public String getSecurityGroup() {
        return securityGroup;
    }

    public String getNetworkSettings() {
        return networkSettings;
    }

    public int getMinInstanceCount() {
        return minInstanceCount;
    }

    public int getMaxInstanceCount() {
        return maxInstanceCount;
    }
}
