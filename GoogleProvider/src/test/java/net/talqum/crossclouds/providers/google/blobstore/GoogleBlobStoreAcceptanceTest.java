package net.talqum.crossclouds.providers.google.blobstore;

import net.talqum.crossclouds.blobstorage.common.Blob;
import net.talqum.crossclouds.blobstorage.common.BlobStoreContext;
import net.talqum.crossclouds.blobstorage.common.DefaultBlob;
import net.talqum.crossclouds.blobstorage.common.Payload;
import net.talqum.crossclouds.blobstorage.exceptions.ContainerNotFoundException;
import net.talqum.crossclouds.blobstorage.payloads.FilePayload;
import net.talqum.crossclouds.blobstorage.payloads.StringPayload;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.providers.ContextFactory;
import net.talqum.crossclouds.providers.google.fixtures.GoogleFixtures;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * Created by Imre on 2015.03.25..
 */
public class GoogleBlobStoreAcceptanceTest {
    private static BlobStoreContext ctx;

    @BeforeClass
    public static void setCtx(){
        ctx = ContextFactory
                .newFactory("google")
                .credentials(GoogleFixtures.ACC_ID, GoogleFixtures.ACC_SECRET)
                .build(BlobStoreContext.class);
    }

    @Before
    public void init(){
        try {
            ctx.getBlobStore().deleteContainer(GoogleFixtures.BUCKET_NAME + 1);

            ctx.getBlobStore().removeBlob(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_IMAGE);
            ctx.getBlobStore().removeBlob(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_STRING);
        } catch (ClientException e) {
            System.err.println("Error occured in the init section. No further action needed.");
        }
    }

    @Test
    public void createContainer(){
        try{
            assertFalse(ctx.getBlobStore().containerExists(GoogleFixtures.BUCKET_NAME + 1));
            assertTrue(ctx.getBlobStore().createContainer(GoogleFixtures.BUCKET_NAME + 1));
            assertTrue(ctx.getBlobStore().containerExists(GoogleFixtures.BUCKET_NAME + 1));
        }catch (ClientException e){
            fail();
        }
    }

    @Test
    public void deleteContainer(){
        try{
            ctx.getBlobStore().createContainer(GoogleFixtures.BUCKET_NAME + 2);
            assertTrue(ctx.getBlobStore().containerExists(GoogleFixtures.BUCKET_NAME + 2));

            ctx.getBlobStore().deleteContainer(GoogleFixtures.BUCKET_NAME + 2);
            assertFalse(ctx.getBlobStore().containerExists(GoogleFixtures.BUCKET_NAME + 2));
        }catch (ClientException e){
            fail();
        }
    }

    @Test
    public void putFileBlob(){
        Payload pl = null;
        try {
            pl = new FilePayload(new File(ClassLoader.getSystemResource(GoogleFixtures.TEST_IMAGE).toURI()));

            boolean success = ctx.getBlobStore().putBlob(GoogleFixtures.BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_IMAGE, pl));

            assertTrue(success);
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }
    }

    @Test
    public void putStringBlob(){
        Payload pl = new StringPayload(GoogleFixtures.TEST_STRING_CONTENT);
        boolean success = false;
        try {
            success = ctx.getBlobStore().putBlob(GoogleFixtures.BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_STRING, pl));
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }

        assertTrue(success);
    }

    @Test
    public void testBlobExist(){
        try{
            boolean exists = ctx.getBlobStore().blobExists(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_STRING + 1);
            assertFalse(exists);

            Payload pl = new StringPayload(GoogleFixtures.TEST_STRING_CONTENT);

            ctx.getBlobStore().putBlob(GoogleFixtures.BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_STRING + 1, pl));

            exists = ctx.getBlobStore().blobExists(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_STRING + 1);
            assertTrue(exists);

            ctx.getBlobStore().removeBlob(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_STRING + 1);

            exists = ctx.getBlobStore().blobExists(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_STRING + 1);
            assertFalse(exists);
        }catch (ClientException e){
            fail();
        }
    }

    @Test
    public void testGetBlob(){
        try{
            // TEXT and IMAGE file upload
            Payload pl = new StringPayload(GoogleFixtures.TEST_STRING_CONTENT);
            ctx.getBlobStore().putBlob(GoogleFixtures.BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_STRING, pl));

            boolean exists = ctx.getBlobStore().blobExists(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_STRING);
            assertTrue(exists);

            pl = new FilePayload(new File(ClassLoader.getSystemResource(GoogleFixtures.TEST_IMAGE).toURI()));
            ctx.getBlobStore().putBlob(GoogleFixtures.BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_IMAGE, pl));

            exists = ctx.getBlobStore().blobExists(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_IMAGE);
            assertTrue(exists);

            // DOWNLOAD
            Blob blob1 = ctx.getBlobStore().getBlob(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_STRING);
            Blob blob2 = ctx.getBlobStore().getBlob(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_IMAGE);

            assertNotNull(blob1);
            assertNotNull(blob2);
            assertNotNull(blob1.getPayload().getRawContent());
            assertNotNull(blob2.getPayload().getRawContent());
            assertEquals(8, blob1.getPayload().getContentLength());
            assertEquals(39482, blob2.getPayload().getContentLength());
        }catch (ClientException e){
            fail();
        } catch (URISyntaxException e) {
            fail();
        }
    }

    @Test
    public void testDeleteBlob(){
        try{
            boolean exists = ctx.getBlobStore().blobExists(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_IMAGE);

            if(!exists){
                Payload pl = new FilePayload(new File(ClassLoader.getSystemResource(GoogleFixtures.TEST_IMAGE).toURI()));
                boolean success = ctx.getBlobStore().putBlob(GoogleFixtures.BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_IMAGE, pl));
                assertTrue(success);
            }

            ctx.getBlobStore().removeBlob(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_IMAGE);

            exists = ctx.getBlobStore().blobExists(GoogleFixtures.BUCKET_NAME, GoogleFixtures.TEST_IMAGE);
            assertFalse(exists);
        }catch (ClientException e){
            fail();
        } catch (URISyntaxException e) {
            fail();
        }
    }

    @Test
    public void clearContainer(){
        try {
            ctx.getBlobStore().clearContainer(GoogleFixtures.BUCKET_NAME);

            assertTrue(ctx.getBlobStore().countBlobs(GoogleFixtures.BUCKET_NAME) == 0);
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }
    }

}
