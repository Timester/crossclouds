package net.talqum.crossclouds.blobstorage.common;

import net.talqum.crossclouds.common.Context;

/**
 * Created by Imre on 2015.03.04..
 */
public interface BlobStoreContext extends Context {
    BlobStore getBlobStore();
}
