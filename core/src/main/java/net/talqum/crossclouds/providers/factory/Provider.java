package net.talqum.crossclouds.providers.factory;

public interface Provider {
    Credentials fromProvider(String providerId);
}
