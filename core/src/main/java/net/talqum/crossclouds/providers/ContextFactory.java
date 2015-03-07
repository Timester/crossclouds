package net.talqum.crossclouds.providers;

import com.google.common.reflect.TypeToken;
import net.talqum.crossclouds.common.Context;

/**
 * Created by Imre on 2015.03.07..
 */
public class ContextFactory {
    private String identity;
    private String secret;
    private final String provider;

    private ContextFactory(String provider) {
        this.provider = provider;
    }

    public static ContextFactory newFactory(String provider){
        return new ContextFactory(provider);
    }

    public ContextFactory credentials(String identity, String secret){
        this.identity = identity;
        this.secret = secret;
        return this;
    }

    public <C extends Context> build(Class<C> contextType){
        /*
        if(provider == null){
            return null;
        }

        if(provider.equals(Provider.AWS)){
            try {
                return new DefaultAWSS3BlobStoreContext(identity, secret);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else if(provider.equals(Provider.AZURE)){
            try{
                return new DefaultAzureBlobStoreContext(identity, secret);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
                return null;
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return null;
            }
        }
        else{
            return null;
        }
        */
        return new Context();
    }
}
