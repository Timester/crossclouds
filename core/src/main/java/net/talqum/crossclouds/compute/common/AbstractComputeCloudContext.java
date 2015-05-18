package net.talqum.crossclouds.compute.common;

import net.talqum.crossclouds.AbstractService;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015.05.17.
 * Time: 11:27
 */
public abstract class AbstractComputeCloudContext extends AbstractService implements ComputeCloudContext {
    private ComputeCloud computeCloud;

    protected AbstractComputeCloudContext() {}

    protected void setComputeCloud(ComputeCloud bs){
        this.computeCloud = bs;
    }

    @Override
    public ComputeCloud getComputeCloud() {
        return computeCloud;
    }
}
