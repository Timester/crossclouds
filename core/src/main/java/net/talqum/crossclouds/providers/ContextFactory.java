package net.talqum.crossclouds.providers;

import net.talqum.crossclouds.common.Context;
import net.talqum.crossclouds.common.ProviderMetadata;
import net.talqum.crossclouds.exceptions.ProviderNotFoundException;

/**
 * Created by Imre on 2015.03.07..
 */
public class ContextFactory {
    private String identity;
    private String secret;

    private ProviderMetadata providerMetadata;

    private ContextFactory(String provider) {
        try {
            this.providerMetadata = Providers.find(provider);
            System.out.println(providerMetadata.getClass());
        } catch (ProviderNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ContextFactory newFactory(String provider){
        return new ContextFactory(provider);
    }

    public ContextFactory credentials(String identity, String secret){
        this.identity = identity;
        this.secret = secret;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <C extends Context> C build(Class<C> contextType) {

        try {

            return (C)providerMetadata.getServices().get(0).getRawType().getConstructor(String.class, String.class).newInstance(identity, secret);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
