package net.talqum.crossclouds.compute.vm;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 08. 09.
 * Time: 14:42
 */
public class CreateRequest {
    private final String name;
    private final String image;
    private final String type;
    private final String keyName;
    private final String zone;

    private CreateRequest(Builder b) {
        this.name = b.name;
        this.image = b.image;
        this.type = b.type;
        this.keyName = b.keyName;
        this.zone = b.zone;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getType() {
        return type;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getZone() {
        return zone;
    }

    public static class Builder {
        private final String name;
        private String image;
        private String type;
        private String keyName;
        private String zone;

        public Builder(String name) {
            this.name = name;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder keyName(String keyName) {
            this.keyName = keyName;
            return this;
        }

        public Builder zone(String zone) {
            this.zone = zone;
            return this;
        }

        public CreateRequest build(){
            return new CreateRequest(this);
        }
    }
}
