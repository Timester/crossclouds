package net.talqum.crossclouds.providers.google.blobstore;

import net.talqum.crossclouds.blobstorage.common.BlobStoreContext;
import net.talqum.crossclouds.blobstorage.common.DefaultBlob;
import net.talqum.crossclouds.blobstorage.common.Payload;
import net.talqum.crossclouds.blobstorage.exceptions.ContainerNotFoundException;
import net.talqum.crossclouds.blobstorage.payloads.FilePayload;
import net.talqum.crossclouds.blobstorage.payloads.StringPayload;
import net.talqum.crossclouds.providers.ContextFactory;
import net.talqum.crossclouds.providers.google.fixtures.GoogleFixtures;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * Created by Imre on 2015.03.25..
 */
public class GoogleBlobStoreTest {
    private static BlobStoreContext ctx;

    @BeforeClass
    public static void setCtx(){
        ctx = ContextFactory
                .newFactory("azure")
                .credentials(GoogleFixtures.ACC_ID, GoogleFixtures.ACC_SECRET)
                .build(BlobStoreContext.class);

        // Clear test data
        try {
            ctx.getBlobStore().deleteContainer(GoogleFixtures.BUCKET_NAME + 1);
        } catch (Exception e){
            System.out.println("No bucket to delete");
        }
        ctx.getBlobStore().removeBlob(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_IMAGE);
        ctx.getBlobStore().removeBlob(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_STRING);

    }

    @Test
    public void createContainer(){
        assertFalse(ctx.getBlobStore().containerExists(GoogleFixtures.BUCKET_NAME + 1));
        assertTrue(ctx.getBlobStore().createContainer(GoogleFixtures.BUCKET_NAME + 1));
        assertTrue(ctx.getBlobStore().containerExists(GoogleFixtures.BUCKET_NAME + 1));
    }

    @Test
    public void deleteContainer(){
        ctx.getBlobStore().createContainer(GoogleFixtures.BUCKET_NAME + 1);
        assertTrue(ctx.getBlobStore().containerExists(GoogleFixtures.BUCKET_NAME + 1));
        ctx.getBlobStore().deleteContainer(GoogleFixtures.BUCKET_NAME + 1);
        assertFalse(ctx.getBlobStore().containerExists(GoogleFixtures.BUCKET_NAME + 1));
    }

    @Test
    public void putFileBlob(){
        Payload pl = null;
        try {
            pl = new FilePayload(new File(ClassLoader.getSystemResource(GoogleFixtures.TEST_IMAGE).toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        boolean success = ctx.getBlobStore().putBlob(GoogleFixtures.BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_IMAGE, pl));

        assertTrue(success);
    }

    @Test
    public void putStringBlob(){
        Payload pl = new StringPayload(GoogleFixtures.TEST_STRING_CONTENT);
        boolean success = ctx.getBlobStore().putBlob(GoogleFixtures.BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_STRING, pl));

        assertTrue(success);
    }

    @Test
    public void deleteBlob(){
        ctx.getBlobStore().removeBlob(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_IMAGE);
    }

    @Test
    public void clearContainer(){
        try {
            ctx.getBlobStore().clearContainer(GoogleFixtures.BUCKET_NAME);
            assertTrue(ctx.getBlobStore().countBlobs(GoogleFixtures.BUCKET_NAME) == 0);
        } catch (ContainerNotFoundException e) {
            fail();
        }
    }

}
