package net.talqum.crossclouds.providers.azure.blobstore;

import net.talqum.crossclouds.blobstorage.common.Blob;
import net.talqum.crossclouds.blobstorage.common.BlobStoreContext;
import net.talqum.crossclouds.blobstorage.common.DefaultBlob;
import net.talqum.crossclouds.blobstorage.common.Payload;
import net.talqum.crossclouds.blobstorage.exceptions.ContainerNotFoundException;
import net.talqum.crossclouds.blobstorage.payloads.FilePayload;
import net.talqum.crossclouds.blobstorage.payloads.StringPayload;
import net.talqum.crossclouds.exceptions.ClientException;
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

            ctx.getBlobStore().removeBlob(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_IMAGE);
            ctx.getBlobStore().removeBlob(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING);
        } catch (ClientException ce){
            fail(ce.getErrorCode().toString());
        } catch (Exception e){
            System.out.println("No bucket to delete");
        }


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

            boolean success = ctx.getBlobStore().putBlob(AzureFixtures.BUCKET_NAME, new DefaultBlob(AzureFixtures.TEST_IMAGE, pl));

            assertTrue(success);
        } catch (URISyntaxException e) {
            e.printStackTrace();
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
    public void testBlobExist(){
        boolean exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING);
        assertFalse(exists);

        Payload pl = new StringPayload(AzureFixtures.TEST_STRING_CONTENT);
        try {
            ctx.getBlobStore().putBlob(AzureFixtures.BUCKET_NAME, new DefaultBlob(AzureFixtures.TEST_STRING, pl));
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }

        exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING);
        assertTrue(exists);

        try {
            ctx.getBlobStore().removeBlob(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING);
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }
        exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_STRING);
        assertFalse(exists);
    }

    @Test
    public void testGetBlob(){
        // TODO finish this
        // TEXT and IMAGE file upload
        /*
        Payload pl = new StringPayload(AWSFixtures.TEST_STRING_CONTENT);
        try {
            ctx.getBlobStore().putBlob(AWSFixtures.BUCKET_NAME, new DefaultBlob(AWSFixtures.TEST_STRING, pl));
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }

        boolean exists = ctx.getBlobStore().blobExists(AWSFixtures.BUCKET_NAME, AWSFixtures.TEST_STRING);
        assertTrue(exists);

        try {
            pl = new FilePayload(new File(ClassLoader.getSystemResource(AWSFixtures.TEST_IMAGE).toURI()));
            ctx.getBlobStore().putBlob(AWSFixtures.BUCKET_NAME, new DefaultBlob(AWSFixtures.TEST_IMAGE, pl));

            exists = ctx.getBlobStore().blobExists(AWSFixtures.BUCKET_NAME, AWSFixtures.TEST_IMAGE);
            assertTrue(exists);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }

        // DOWNLOAD
        Blob blob1 = ctx.getBlobStore().getBlob(AWSFixtures.BUCKET_NAME, AWSFixtures.TEST_STRING);
        Blob blob2 = ctx.getBlobStore().getBlob(AWSFixtures.BUCKET_NAME, AWSFixtures.TEST_IMAGE);

        assertNotNull(blob1);
        assertNotNull(blob2);
        assertNotNull(blob1.getPayload().getRawContent());
        assertNotNull(blob2.getPayload().getRawContent());
        assertEquals(8, blob1.getPayload().getContentLength());
        assertEquals(39482, blob2.getPayload().getContentLength());
        */
    }

    @Test
    public void testDeleteBlob(){
        boolean exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_IMAGE);
        assertTrue(exists);
        try {
            ctx.getBlobStore().removeBlob(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_IMAGE);
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }

        exists = ctx.getBlobStore().blobExists(AzureFixtures.BUCKET_NAME, AzureFixtures.TEST_IMAGE);
        assertFalse(exists);
    }

    @Test
    public void clearContainer(){

        try {
            ctx.getBlobStore().clearContainer(AzureFixtures.BUCKET_NAME);
        } catch (ClientException e) {
            fail(e.getErrorCode().toString());
        }
        assertTrue(ctx.getBlobStore().countBlobs(AzureFixtures.BUCKET_NAME) == 0);
    }

}
