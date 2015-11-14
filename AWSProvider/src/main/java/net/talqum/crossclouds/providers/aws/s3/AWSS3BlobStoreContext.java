package net.talqum.crossclouds.providers.aws.s3;

import net.talqum.crossclouds.blobstorage.common.BlobStoreContext;

public interface AWSS3BlobStoreContext extends BlobStoreContext {

    @Override
    AWSS3BlobStore getBlobStore();
}
