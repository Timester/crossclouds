package net.talqum.crossclouds.providers;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 10. 05.
 * Time: 16:19
 */
public class ContextConfig {
    private String providerId;

    private CredentialType credentialType;

    private String accId;
    private String appName;
    private String keyPath;

    private String id;
    private String secret;

    private boolean isAsync;

    private String location;

    private Class contextType;

    public ContextConfig(Class c) {
        this.contextType = c;
    }

    public String getProviderId() {
        return providerId;
    }

    public CredentialType getCredentialType() {
        return credentialType;
    }

    public String getAccId() {
        return accId;
    }

    public String getAppName() {
        return appName;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public String getId() {
        return id;
    }

    public String getSecret() {
        return secret;
    }

    public boolean isAsync() {
        return isAsync;
    }

    public String getLocation() {
        return location;
    }

    public Class getContextType() {
        return contextType;
    }

    public ContextConfig setProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public ContextConfig setCredentialType(CredentialType credentialType) {
        this.credentialType = credentialType;
        return this;
    }

    public ContextConfig setAccId(String accId) {
        this.accId = accId;
        return this;
    }

    public ContextConfig setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public ContextConfig setKeyPath(String keyPath) {
        this.keyPath = keyPath;
        return this;
    }

    public ContextConfig setId(String id) {
        this.id = id;
        return this;
    }

    public ContextConfig setSecret(String secret) {
        this.secret = secret;
        return this;
    }

    public ContextConfig setIsAsync(boolean isAsync) {
        this.isAsync = isAsync;
        return this;
    }

    public ContextConfig setLocation(String location) {
        this.location = location;
        return this;
    }

    public ContextConfig setContextType(Class contextType) {
        this.contextType = contextType;
        return this;
    }

    @Override
    public String toString() {
        return "ContextConfig{" +
                "accId='" + accId + '\'' +
                ", applicationName='" + appName + '\'' +
                ", keyPath='" + keyPath + '\'' +
                ", id='" + id + '\'' +
                ", secret='" + secret + '\'' +
                ", isAsync=" + isAsync +
                ", location='" + location + '\'' +
                ", contextType=" + contextType +
                '}';
    }
}
