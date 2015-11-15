package net.talqum.crossclouds.blobstorage.payloads;

import com.google.common.net.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Imre on 2015.03.04..
 */
public class FilePayload extends AbstractPayload<File> {
    public FilePayload(File data) {
        super(data, data.length());
        checkNotNull(data, "content");
        this.contentType = MediaType.ANY_TYPE;
    }

    public FilePayload(File data, MediaType contentType) {
        super(data, data.length());
        checkNotNull(data, "content");
        this.contentType = contentType;
    }

    @Override
    public InputStream openStream() throws IOException {
        return new FileInputStream(data);
    }
}
