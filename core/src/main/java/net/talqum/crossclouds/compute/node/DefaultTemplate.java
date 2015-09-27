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

    DefaultTemplate(String name, Image image, Hardware hardware, Location location, Options options) {
        this.image = image;
        this.hardware = hardware;
        this.location = location;
        this.name = name;
        this.options = options;
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
