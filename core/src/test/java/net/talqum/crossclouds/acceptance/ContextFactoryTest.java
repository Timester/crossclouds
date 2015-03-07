package net.talqum.crossclouds.acceptance;

import net.talqum.crossclouds.blobstorage.common.BlobStoreContext;
import net.talqum.crossclouds.providers.ContextFactory;
import org.junit.Test;

/**
 * Created by Imre on 2015.03.07..
 */
public class ContextFactoryTest {

    @Test
    public void testInit(){
        BlobStoreContext ctx = ContextFactory.newFactory("aws").credentials("a", "b").build(BlobStoreContext.class);
    }
}
