package net.talqum.crossclouds.blobstorage.common;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Imre on 2015.03.04..
 */
public interface Payload {
    InputStream openStream() throws IOException;
    Object getRawContent();
    long getContentLength();
}
