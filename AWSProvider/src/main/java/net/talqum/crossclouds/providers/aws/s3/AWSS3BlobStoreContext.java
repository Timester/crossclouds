package net.talqum.crossclouds.providers.aws.s3;

import net.talqum.crossclouds.blobstorage.common.BlobStoreContext;

/**
 * Created by Imre on 2015.03.04..
 */
public interface AWSS3BlobStoreContext extends BlobStoreContext {

    @Override
    AWSS3BlobStore getBlobStore();
}
