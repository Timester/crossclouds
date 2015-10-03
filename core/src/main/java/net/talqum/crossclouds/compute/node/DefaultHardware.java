package net.talqum.crossclouds.compute.node;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 10. 03.
 * Time: 16:37
 */
public class DefaultHardware implements Hardware {
    private final String configId;

    private DefaultHardware(Builder builder) {
        this.configId = builder.configId;
    }

    public static class Builder {
        private final String configId;

        public Builder(String configId) {
            this.configId = configId;
        }

        public DefaultHardware build() {
            return new DefaultHardware(this);
        }
    }

    @Override
    public String getConfigId() {
        return this.configId;
    }
}
