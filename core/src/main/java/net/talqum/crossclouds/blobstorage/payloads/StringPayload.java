package net.talqum.crossclouds.blobstorage.payloads;

import com.google.common.base.Charsets;
import com.google.common.net.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Imre on 2015.03.04..
 */
public class StringPayload extends AbstractPayload<String> {
    private final byte[] bytes;

    public StringPayload(String data) {
        super(data, data.getBytes(Charsets.UTF_8).length);
        this.bytes = data.getBytes(Charsets.UTF_8);
        this.contentType = MediaType.ANY_TEXT_TYPE;
    }

    @Override
    public InputStream openStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }
}
