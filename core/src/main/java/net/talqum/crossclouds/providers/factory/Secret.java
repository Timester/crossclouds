package net.talqum.crossclouds.providers.factory;

public interface Secret extends End {
    Async secret(String secret);
}
