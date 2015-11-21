package net.talqum.crossclouds.compute.node;

public class Template {

    private final String name;

    private final Image image;
    private final Hardware hardware;
    private final Options options;

    private Template(Builder builder) {
        this.image = builder.image;
        this.hardware = builder.hardware;
        this.name = builder.name;
        this.options = builder.options;
    }

    public static class Builder {

        private final String name;
        private Image image;
        private Hardware hardware;
        private Options options;

        public Builder(String name) {
            this.name = name;
        }

        public Builder image(Image image) {
            this.image = image;
            return this;
        }

        public Builder hardware(Hardware hardware) {
            this.hardware = hardware;
            return this;
        }

        public Builder options(Options options) {
            this.options = options;
            return this;
        }

        public Template build() {
            return new Template(this);
        }
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    public Hardware getHardware() {
        return hardware;
    }

    public Options getOptions() {
        return options;
    }
}
