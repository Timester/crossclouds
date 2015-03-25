package net.talqum.crossclouds.providers.aws.s3;

import net.talqum.crossclouds.blobstorage.common.BlobStoreContext;
import net.talqum.crossclouds.blobstorage.common.DefaultBlob;
import net.talqum.crossclouds.blobstorage.common.Payload;
import net.talqum.crossclouds.blobstorage.exceptions.ContainerNotFoundException;
import net.talqum.crossclouds.blobstorage.payloads.FilePayload;
import net.talqum.crossclouds.blobstorage.payloads.StringPayload;
import net.talqum.crossclouds.providers.ContextFactory;
import net.talqum.crossclouds.providers.aws.fixtures.AWSFixtures;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Imre on 2015.03.25..
 */
public class AWSS3BlobStoreTest {

    private static BlobStoreContext ctx;

    @BeforeClass
    public static void setCtx(){
        ctx = ContextFactory
                .newFactory("aws")
                .credentials(AWSFixtures.ACC_ID, AWSFixtures.ACC_SECRET)
                .build(BlobStoreContext.class);

        // Clear test data
        try {
            ctx.getBlobStore().deleteContainer(AWSFixtures.BUCKET_NAME + 1);
        } catch (Exception e){
            System.out.println("No bucket to delete");
        }
        ctx.getBlobStore().removeBlob(AWSFixtures.BUCKET_NAME, AWSFixtures.TEST_IMAGE);
        ctx.getBlobStore().removeBlob(AWSFixtures.BUCKET_NAME, AWSFixtures.TEST_STRING);

    }

    @Test
    public void createContainer(){
        assertFalse(ctx.getBlobStore().containerExists(AWSFixtures.BUCKET_NAME + 1));
        assertTrue(ctx.getBlobStore().createContainer(AWSFixtures.BUCKET_NAME + 1));
        assertTrue(ctx.getBlobStore().containerExists(AWSFixtures.BUCKET_NAME + 1));
    }

    @Test
    public void deleteContainer(){
        ctx.getBlobStore().createContainer(AWSFixtures.BUCKET_NAME + 1);
        assertTrue(ctx.getBlobStore().containerExists(AWSFixtures.BUCKET_NAME + 1));
        ctx.getBlobStore().deleteContainer(AWSFixtures.BUCKET_NAME + 1);
        assertFalse(ctx.getBlobStore().containerExists(AWSFixtures.BUCKET_NAME + 1));
    }

    @Test
    public void putFileBlob(){
        Payload pl = null;
        try {
            pl = new FilePayload(new File(ClassLoader.getSystemResource(AWSFixtures.TEST_IMAGE).toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        boolean success = ctx.getBlobStore().putBlob(AWSFixtures.BUCKET_NAME, new DefaultBlob(AWSFixtures.TEST_IMAGE, pl));

        assertTrue(success);
    }

    @Test
    public void putStringBlob(){
        Payload pl = new StringPayload(AWSFixtures.TEST_STRING_CONTENT);
        boolean success = ctx.getBlobStore().putBlob(AWSFixtures.BUCKET_NAME, new DefaultBlob(AWSFixtures.TEST_STRING, pl));

        assertTrue(success);
    }

    @Test
    public void deleteBlob(){
        ctx.getBlobStore().removeBlob(AWSFixtures.BUCKET_NAME, AWSFixtures.TEST_IMAGE);
    }

    @Test
    public void clearContainer(){
        try {
            assertFalse(ctx.getBlobStore().countBlobs(AWSFixtures.BUCKET_NAME) == 0);
            ctx.getBlobStore().clearContainer(AWSFixtures.BUCKET_NAME);
            assertTrue(ctx.getBlobStore().countBlobs(AWSFixtures.BUCKET_NAME) == 0);
        } catch (ContainerNotFoundException e) {
            fail();
        }
    }

}