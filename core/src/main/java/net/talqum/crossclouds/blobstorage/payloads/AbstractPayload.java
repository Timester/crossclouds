package net.talqum.crossclouds.blobstorage.payloads;

import com.google.common.net.MediaType;
import net.talqum.crossclouds.blobstorage.common.Payload;

/**
 * Created by Imre on 2015.03.04..
 */
public abstract class AbstractPayload<T> implements Payload {
    protected T data;
    protected long contentlength;
    protected MediaType contentType;

    public AbstractPayload(T data, long length) {
        this.data = data;
        this.contentlength = length;
    }

    public MediaType getContentType() {
        return contentType;
    }

    @Override
    public T getRawContent() {
        return data;
    }

    @Override
    public long getContentLength(){
        return contentlength;
    }
}
