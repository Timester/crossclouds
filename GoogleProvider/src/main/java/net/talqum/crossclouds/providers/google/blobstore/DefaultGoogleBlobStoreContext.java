package net.talqum.crossclouds.providers.google.blobstore;

import net.talqum.crossclouds.blobstorage.common.AbstractBlobStoreContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by Imre on 2015.03.04..
 */
public class DefaultGoogleBlobStoreContext extends AbstractBlobStoreContext implements GoogleBlobStoreContext {



    public DefaultGoogleBlobStoreContext(String identity, String secret) throws URISyntaxException, InvalidKeyException{
        super();

    }

    @Override
    public GoogleBlobStore getBlobStore() {
        return GoogleBlobStore.class.cast(super.getBlobStore());
    }

    @Override
    public void close() throws IOException {
        // TODO
    }
}
