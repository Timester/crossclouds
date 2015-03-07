package net.talqum.crossclouds.blobstorage.common;

/**
 * Created by Imre on 2015.03.04..
 */
public interface Blob {
    String getName();
    Payload getPayload();
}