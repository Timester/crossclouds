package net.talqum.crossclouds.providers.azure.blobstore;

import net.talqum.crossclouds.blobstorage.common.BlobStoreContext;
import net.talqum.crossclouds.blobstorage.common.DefaultBlob;
import net.talqum.crossclouds.blobstorage.common.Payload;
import net.talqum.crossclouds.blobstorage.exceptions.ContainerNotFoundException;
import net.talqum.crossclouds.blobstorage.payloads.FilePayload;
import net.talqum.crossclouds.blobstorage.payloads.StringPayload;
import net.talqum.crossclouds.providers.ContextFactory;
import net.talqum.crossclouds.providers.azure.fixtures.AzureFixtures;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * Created by Imre on 2015.03.25..
 */
public class AzureBlobStoreTest {
    private static BlobStoreContext ctx;

    @BeforeClass
    public static void setCtx(){
        ctx = ContextFactory
                .newFactory("azure")
                .credentials(AzureFixtures.ACC_ID, AzureFixtures.ACC_SECRET)
                .build(BlobStoreContext.class);

        // Clear test data
        try {
            ctx.getBlobStore().deleteContainer(AzureFixtures.BUCKET_NAME + 1);
        } catch (Exception e){
            System.out.println("No bucket to delete");
        }
        ctx.getBlobStore().removeBlob(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_IMAGE);
        ctx.getBlobStore().removeBlob(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING);

    }

    @Test
    public void createContainer(){
        assertFalse(ctx.getBlobStore().containerExists(AzureFixtures.BUCKET_NAME + 1));
        assertTrue(ctx.getBlobStore().createContainer(AzureFixtures.BUCKET_NAME + 1));
        assertTrue(ctx.getBlobStore().containerExists(AzureFixtures.BUCKET_NAME + 1));
    }

    @Test
    public void deleteContainer(){
        ctx.getBlobStore().createContainer(AzureFixtures.BUCKET_NAME + 1);
        assertTrue(ctx.getBlobStore().containerExists(AzureFixtures.BUCKET_NAME + 1));
        ctx.getBlobStore().deleteContainer(AzureFixtures.BUCKET_NAME + 1);
        assertFalse(ctx.getBlobStore().containerExists(AzureFixtures.BUCKET_NAME + 1));
    }

    @Test
    public void putFileBlob(){
        Payload pl = null;
        try {
            pl = new FilePayload(new File(ClassLoader.getSystemResource(AzureFixtures.TEST_IMAGE).toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        boolean success = ctx.getBlobStore().putBlob(AzureFixtures.BUCKET_NAME, new DefaultBlob(AzureFixtures.TEST_IMAGE, pl));

        assertTrue(success);
    }

    @Test
    public void putStringBlob(){
        Payload pl = new StringPayload(AzureFixtures.TEST_STRING_CONTENT);
        boolean success = ctx.getBlobStore().putBlob(AzureFixtures.BUCKET_NAME, new DefaultBlob(AzureFixtures.TEST_STRING, pl));

        assertTrue(success);
    }

    @Test
    public void testBlobExist(){
        boolean exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING + "alma");
        assertFalse(exists);

        Payload pl = new StringPayload(AzureFixtures.TEST_STRING_CONTENT);
        ctx.getBlobStore().putBlob(AzureFixtures.BUCKET_NAME, new DefaultBlob(AzureFixtures.TEST_STRING + "alma", pl));

        exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING + "alma");
        assertTrue(exists);

        ctx.getBlobStore().removeBlob(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING + "alma");
        exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING + "alma");
        assertFalse(exists);
    }

    @Test
    public void testDeleteBlob(){
        boolean exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_IMAGE);
        assertTrue(exists);
        ctx.getBlobStore().removeBlob(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_IMAGE);

        exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_IMAGE);
        assertFalse(exists);
    }

    @Test
    public void clearContainer(){
        try {
            ctx.getBlobStore().clearContainer(AzureFixtures.BUCKET_NAME);
            assertTrue(ctx.getBlobStore().countBlobs(AzureFixtures.BUCKET_NAME) == 0);
        } catch (ContainerNotFoundException e) {
            fail();
        }
    }

}
