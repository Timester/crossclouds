package net.talqum.crossclouds.compute.common;

import net.talqum.crossclouds.common.Context;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015.05.17.
 * Time: 11:20
 */
public interface ComputeCloudContext extends Context {
    ComputeCloud getComputeCloud();
}
