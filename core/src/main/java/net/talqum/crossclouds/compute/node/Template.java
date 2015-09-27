package net.talqum.crossclouds.compute.node;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 09. 27.
 * Time: 9:10
 */
public interface Template {

    String getName();

    Image getImage();

    Hardware getHardware();

    Location getLocation();

    Options getOptions();
}
