package net.talqum.crossclouds.providers.azure.blobstore;

import net.talqum.crossclouds.blobstorage.common.Blob;
import net.talqum.crossclouds.blobstorage.common.BlobStoreContext;
import net.talqum.crossclouds.blobstorage.common.DefaultBlob;
import net.talqum.crossclouds.blobstorage.common.Payload;
import net.talqum.crossclouds.blobstorage.payloads.FilePayload;
import net.talqum.crossclouds.blobstorage.payloads.StringPayload;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.providers.ContextFactory;
import net.talqum.crossclouds.providers.azure.fixtures.AzureFixtures;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Imre on 2015.03.25..
 */
public class AzureBlobStoreAcceptanceTest {
    private static BlobStoreContext ctx;

    @BeforeClass
    public static void setCtx(){
        ctx = ContextFactory
                .newFactory("azure")
                .credentials(AzureFixtures.ACC_ID, AzureFixtures.ACC_SECRET)
                .build(BlobStoreContext.class);
    }

    @Before
    public void init(){
        try {
            ctx.getBlobStore().deleteContainer(AzureFixtures.BUCKET_NAME + 1);

            ctx.getBlobStore().removeBlob(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_IMAGE);
            ctx.getBlobStore().removeBlob(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING);
        } catch (ClientException e) {
            System.err.println("Error occured in the init section. No further action needed.");
        }
    }

    @Test
    public void containerExists() {
        try {
            assertFalse(ctx.getBlobStore().containerExists(AzureFixtures.BUCKET_NAME + 1));
            assertTrue(ctx.getBlobStore().containerExists(AzureFixtures.EXISTING_BUCKET_NAME));
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }
    }

    @Test
    public void createContainer(){
        try {
            assertFalse(ctx.getBlobStore().containerExists(AzureFixtures.BUCKET_NAME + 1));
            assertTrue(ctx.getBlobStore().createContainer(AzureFixtures.BUCKET_NAME + 1));
            assertTrue(ctx.getBlobStore().containerExists(AzureFixtures.BUCKET_NAME + 1));
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }
    }

    @Test
    public void listContainerContent() {
        try {
            Set<String> content = ctx.getBlobStore().listContainerContent(AzureFixtures.EXISTING_BUCKET_NAME);

            assertEquals(2, content.size());
            assertTrue(content.contains(AzureFixtures.TEST_IMAGE));
            assertTrue(content.contains(AzureFixtures.TEST_STRING));

            ctx.getBlobStore().listContainerContent("nonexistent_container");
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }
    }

    @Test
    public void clearContainer(){
        try {
            ctx.getBlobStore().clearContainer(AzureFixtures.BUCKET_NAME);

            assertTrue(ctx.getBlobStore().countBlobs(AzureFixtures.BUCKET_NAME) == 0);
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }
    }

    @Test
    public void deleteContainer(){
        try{
            ctx.getBlobStore().createContainer(AzureFixtures.BUCKET_NAME + 2);
            assertTrue(ctx.getBlobStore().containerExists(AzureFixtures.BUCKET_NAME + 2));

            ctx.getBlobStore().deleteContainer(AzureFixtures.BUCKET_NAME + 2);
            assertFalse(ctx.getBlobStore().containerExists(AzureFixtures.BUCKET_NAME + 2));
        }catch (ClientException e){
            fail();
        }
    }

    @Test
    public void putFileBlob(){
        Payload pl;
        try {
            pl = new FilePayload(new File(ClassLoader.getSystemResource(AzureFixtures.TEST_IMAGE).toURI()));

            boolean success = ctx.getBlobStore().putBlob(AzureFixtures.BUCKET_NAME, new DefaultBlob(AzureFixtures.TEST_IMAGE, pl));

            assertTrue(success);
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }
    }

    @Test
    public void putStringBlob(){
        Payload pl = new StringPayload(AzureFixtures.TEST_STRING_CONTENT);
        boolean success = false;
        try {
            success = ctx.getBlobStore().putBlob(AzureFixtures.BUCKET_NAME, new DefaultBlob(AzureFixtures.TEST_STRING, pl));
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }

        assertTrue(success);
    }

    @Test
    public void blobExist(){
        try{
            boolean exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING + 1);
            assertFalse(exists);

            Payload pl = new StringPayload(AzureFixtures.TEST_STRING_CONTENT);

            ctx.getBlobStore().putBlob(AzureFixtures.BUCKET_NAME, new DefaultBlob(AzureFixtures.TEST_STRING + 1, pl));

            exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING + 1);
            assertTrue(exists);

            ctx.getBlobStore().removeBlob(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING + 1);

            exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING + 1);
            assertFalse(exists);
        }catch (ClientException e){
            fail(e.getErrorCode().toString());
        }
    }

    @Test
    public void countBlobs() {
        try {
            long blobs = ctx.getBlobStore().countBlobs(AzureFixtures.EXISTING_BUCKET_NAME);

            assertEquals(2, blobs);

            blobs = ctx.getBlobStore().countBlobs("nonexistent_bucket");

            assertEquals(0, blobs);
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }
    }

    @Test
    public void getBlob(){
        try{
            // TEXT and IMAGE file upload
            Payload pl = new StringPayload(AzureFixtures.TEST_STRING_CONTENT);
            ctx.getBlobStore().putBlob(AzureFixtures.BUCKET_NAME, new DefaultBlob(AzureFixtures.TEST_STRING, pl));

            boolean exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING);
            assertTrue(exists);

            pl = new FilePayload(new File(ClassLoader.getSystemResource(AzureFixtures.TEST_IMAGE).toURI()));
            ctx.getBlobStore().putBlob(AzureFixtures.BUCKET_NAME, new DefaultBlob(AzureFixtures.TEST_IMAGE, pl));

            exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_IMAGE);
            assertTrue(exists);

            // DOWNLOAD
            Blob blob1 = ctx.getBlobStore().getBlob(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING);
            Blob blob2 = ctx.getBlobStore().getBlob(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_IMAGE);

            assertNotNull(blob1);
            assertNotNull(blob2);
            assertNotNull(blob1.getPayload().getRawContent());
            assertNotNull(blob2.getPayload().getRawContent());
            assertEquals(8, blob1.getPayload().getContentLength());
            assertEquals(39482, blob2.getPayload().getContentLength());
        }catch (ClientException e){
            fail(e.getErrorCode().toString());
        } catch (URISyntaxException e) {
            fail();
        }
    }

    @Test
    public void deleteBlob(){
        try {
            boolean exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_IMAGE);

            if(!exists){
                Payload pl = new FilePayload(new File(ClassLoader.getSystemResource(AzureFixtures.TEST_IMAGE).toURI()));
                boolean success = ctx.getBlobStore().putBlob(AzureFixtures.BUCKET_NAME, new DefaultBlob(AzureFixtures.TEST_IMAGE, pl));
                assertTrue(success);
            }

            ctx.getBlobStore().removeBlob(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_IMAGE);

            exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_IMAGE);
            assertFalse(exists);
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        } catch (URISyntaxException e) {
            fail();
        }
    }



}
