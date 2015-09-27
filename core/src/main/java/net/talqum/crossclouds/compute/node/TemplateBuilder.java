package net.talqum.crossclouds.compute.node;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 09. 27.
 * Time: 9:10
 */
public interface TemplateBuilder {
    TemplateBuilder image(Image image);
    TemplateBuilder hardware(Hardware hardware);
    TemplateBuilder location(Location location);
    TemplateBuilder options(Options options);

    Template build();
}
