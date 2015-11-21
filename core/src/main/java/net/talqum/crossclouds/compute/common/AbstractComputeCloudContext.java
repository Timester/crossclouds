package net.talqum.crossclouds.compute.common;

public abstract class AbstractComputeCloudContext implements ComputeCloudContext {

    private ComputeCloud computeCloud;
    protected boolean async;
    protected String location;

    protected AbstractComputeCloudContext() {}

    protected void setComputeCloud(ComputeCloud bs){
        this.computeCloud = bs;
    }

    @Override
    public ComputeCloud getComputeCloud() {
        return computeCloud;
    }


    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
