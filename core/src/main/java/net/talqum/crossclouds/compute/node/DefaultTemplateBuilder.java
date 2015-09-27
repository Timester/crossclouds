package net.talqum.crossclouds.compute.node;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 09. 27.
 * Time: 9:20
 */
public class DefaultTemplateBuilder implements TemplateBuilder {

    private String name;

    private Image image;
    private Hardware hardware;
    private Location location;
    private Options options;

    public DefaultTemplateBuilder(String name) {
        this.name = name;
    }

    @Override
    public TemplateBuilder image(Image image) {
        this.image = image;
        return this;
    }

    @Override
    public TemplateBuilder hardware(Hardware hardware) {
        this.hardware = hardware;
        return this;
    }

    @Override
    public TemplateBuilder location(Location location) {
        this.location = location;
        return this;
    }

    @Override
    public TemplateBuilder options(Options options) {
        this.options = options;
        return this;
    }

    @Override
    public Template build() {
        return new DefaultTemplate(name, image, hardware, location, options);
    }
}
