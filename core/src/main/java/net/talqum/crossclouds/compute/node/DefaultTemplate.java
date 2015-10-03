package net.talqum.crossclouds.compute.node;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 09. 27.
 * Time: 9:17
 */
public class DefaultTemplate implements Template {

    private final String name;

    private final Image image;
    private final Hardware hardware;
    private final Location location;
    private final Options options;

    private DefaultTemplate(Builder builder) {
        this.image = builder.image;
        this.hardware = builder.hardware;
        this.location = builder.location;
        this.name = builder.name;
        this.options = builder.options;
    }

    public static class Builder {

        private final String name;
        private Image image;
        private Hardware hardware;
        private Location location;
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

        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        public Builder options(Options options) {
            this.options = options;
            return this;
        }

        public Template build() {
            return new DefaultTemplate(this);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public Hardware getHardware() {
        return hardware;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public Options getOptions() {
        return options;
    }
}
