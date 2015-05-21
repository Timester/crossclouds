package net.talqum.crossclouds.compute.common;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015.05.17.
 * Time: 11:27
 */
public abstract class AbstractComputeCloudContext implements ComputeCloudContext {
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
