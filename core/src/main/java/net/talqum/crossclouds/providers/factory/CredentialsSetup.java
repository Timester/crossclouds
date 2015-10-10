package net.talqum.crossclouds.providers.factory;

public interface CredentialsSetup {
    KeyCred keyBased();
    IdSecCred idAndSecretBased();
}
