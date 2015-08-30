package net.talqum.crossclouds.blobstorage.common;

/**
 * Created by Imre on 2015.03.04..
 */
public class DefaultBlob implements Blob {
    private final String name;
    private Payload payload;

    public DefaultBlob(String name, Payload payload) {
        this.name = name;
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultBlob)) return false;

        DefaultBlob that = (DefaultBlob) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Payload getPayload() {
        return this.payload;
    }
}
