package net.talqum.crossclouds.compute.common;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015.05.17.
 * Time: 11:27
 */
public abstract class AbstractComputeCloud implements ComputeCloud {

    protected final ComputeCloudContext context;

    protected AbstractComputeCloud(ComputeCloudContext context) {
        this.context = context;
    }

    @Override
    public ComputeCloudContext getContext() {
        return context;
    }
}
