package net.talqum.crossclouds.compute.node;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 10. 03.
 * Time: 16:22
 */
public class DefaultImage {
    private final String operatingSystem;
    private final String credentials;

    private DefaultImage(Builder b) {
        this.operatingSystem = b.operatingSystem;
        this.credentials = b.credentials;
    }

    private static class Builder {
        private final String operatingSystem;
        private String credentials;

        public Builder(String operatingSystem){
            this.operatingSystem = operatingSystem;
        }

        public Builder credentials(String credentials) {
            this.credentials = credentials;
            return this;
        }

        public DefaultImage build() {
            return new DefaultImage(this);
        }
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getCredentials() {
        return credentials;
    }
}
