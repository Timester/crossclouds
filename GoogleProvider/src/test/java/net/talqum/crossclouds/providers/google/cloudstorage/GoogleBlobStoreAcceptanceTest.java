package net.talqum.crossclouds.providers.google.cloudstorage;

import net.talqum.crossclouds.blobstorage.common.*;
import net.talqum.crossclouds.blobstorage.payloads.FilePayload;
import net.talqum.crossclouds.blobstorage.payloads.StringPayload;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.providers.ContextFactory;
import net.talqum.crossclouds.providers.google.fixtures.GoogleFixtures;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Imre on 2015.03.25..
 */
public class GoogleBlobStoreAcceptanceTest {
    private static BlobStoreContext ctx;
    private static BlobStore blobStore;

    @BeforeClass
    public static void setCtx(){
        ctx = ContextFactory
                .newFactory("google")
                .credentials(GoogleFixtures.GOOGLE_CREDENTIALS)
                .applicationName(GoogleFixtures.APP_ID)
                .accountID(GoogleFixtures.SERVICE_ACC_ID)
                .build(BlobStoreContext.class);

        blobStore = ctx.getBlobStore();
    }

    @Before
    public void init() {
        try {
            blobStore.deleteContainer(GoogleFixtures.TEMP_BUCKET_NAME + 1);

            blobStore.createContainer(GoogleFixtures.TEMP_BUCKET_NAME);
            blobStore.clearContainer(GoogleFixtures.TEMP_BUCKET_NAME);
        } catch (ClientException e) {
            System.err.println("Error occured in the init section. No further action needed.");
        }
    }

    @Test
    public void containerExists() throws Exception {
        assertFalse(blobStore.containerExists(GoogleFixtures.NONEXISTENT_BUCKET_NAME));
        assertTrue(blobStore.containerExists(GoogleFixtures.EXISTING_NONEMPTY_BUCKET_NAME));
    }

    @Test
    public void listContainers() throws Exception {
        Set<String> strings = blobStore.listContainers();
    }

    @Test
    public void createContainerIfpreviouslyWasNonexistent() throws Exception {
        assertFalse(blobStore.containerExists(GoogleFixtures.TEMP_BUCKET_NAME + 1));
        assertTrue(blobStore.createContainer(GoogleFixtures.TEMP_BUCKET_NAME + 1));
        assertTrue(blobStore.containerExists(GoogleFixtures.TEMP_BUCKET_NAME + 1));
    }

    @Test
    public void createContainerIfpreviouslyWasExistent() throws Exception {
        assertTrue(blobStore.containerExists(GoogleFixtures.EXISTING_NONEMPTY_BUCKET_NAME));
        assertFalse(blobStore.createContainer(GoogleFixtures.EXISTING_NONEMPTY_BUCKET_NAME));
    }

    @Test
    public void listContainerContentIfExistsAndNotEmpty() throws Exception {
        Set<String> content = blobStore.listContainerContent(GoogleFixtures.EXISTING_NONEMPTY_BUCKET_NAME);

        assertNotNull(content);
        assertEquals(2, content.size());
        assertTrue(content.contains(GoogleFixtures.TEST_IMAGE));
        assertTrue(content.contains(GoogleFixtures.TEST_STRING));
    }

    @Test
    public void listContainerContentIfExistsAndEmpty() throws Exception {
        Set<String> content = blobStore.listContainerContent(GoogleFixtures.EXISTING_EMPTY_BUCKET_NAME);

        assertNotNull(content);
        assertEquals(0, content.size());
    }

    @Test
    public void listContainerContentIfNotExists() throws Exception {
        Set<String> content = blobStore.listContainerContent(GoogleFixtures.NONEXISTENT_BUCKET_NAME);

        assertNotNull(content);
        assertEquals(0, content.size());
    }

    @Test
    public void clearContainerIfExists() throws Exception {
        Payload pl = new StringPayload(GoogleFixtures.TEST_STRING_CONTENT);
        blobStore.putBlob(GoogleFixtures.TEMP_BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_STRING, pl));

        assertEquals(1, blobStore.countBlobs(GoogleFixtures.TEMP_BUCKET_NAME));

        blobStore.clearContainer(GoogleFixtures.TEMP_BUCKET_NAME);

        assertEquals(0, blobStore.countBlobs(GoogleFixtures.TEMP_BUCKET_NAME));
    }

    @Test
    public void clearContainerIfNotExists() throws Exception {
        blobStore.clearContainer(GoogleFixtures.NONEXISTENT_BUCKET_NAME);
    }

    @Test
    public void deleteContainerIfExists() throws Exception {
        assertTrue(blobStore.containerExists(GoogleFixtures.TEMP_BUCKET_NAME));
        blobStore.deleteContainer(GoogleFixtures.TEMP_BUCKET_NAME);
        assertFalse(blobStore.containerExists(GoogleFixtures.TEMP_BUCKET_NAME));
    }

    @Test
    public void deleteContainerIfNotExists() throws Exception {
        blobStore.deleteContainer(GoogleFixtures.NONEXISTENT_BUCKET_NAME);
    }

    @Test
    public void putFileBlob() throws Exception {
        Payload pl;
        pl = new FilePayload(new File(ClassLoader.getSystemResource(GoogleFixtures.TEST_IMAGE).toURI()));

        boolean success = blobStore.putBlob(GoogleFixtures.TEMP_BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_IMAGE, pl));

        assertTrue(success);
    }

    @Test
    public void putStringBlob() throws Exception {
        Payload pl = new StringPayload(GoogleFixtures.TEST_STRING_CONTENT);

        boolean success = blobStore.putBlob(GoogleFixtures.TEMP_BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_STRING, pl));

        assertTrue(success);
    }

    @Test
    public void blobExist() throws Exception {
        boolean exists = blobStore.blobExists(GoogleFixtures.TEMP_BUCKET_NAME, GoogleFixtures.TEST_STRING);
        assertFalse(exists);

        Payload pl = new StringPayload(GoogleFixtures.TEST_STRING_CONTENT);

        blobStore.putBlob(GoogleFixtures.TEMP_BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_STRING, pl));

        exists = blobStore.blobExists(GoogleFixtures.TEMP_BUCKET_NAME, GoogleFixtures.TEST_STRING);
        assertTrue(exists);

        blobStore.removeBlob(GoogleFixtures.TEMP_BUCKET_NAME, GoogleFixtures.TEST_STRING);

        exists = blobStore.blobExists(GoogleFixtures.TEMP_BUCKET_NAME, GoogleFixtures.TEST_STRING);
        assertFalse(exists);

        exists = blobStore.blobExists(GoogleFixtures.NONEXISTENT_BUCKET_NAME, GoogleFixtures.TEST_STRING);
        assertFalse(exists);
    }

    @Test
    public void countBlobs() throws Exception {
        long blobs = blobStore.countBlobs(GoogleFixtures.EXISTING_NONEMPTY_BUCKET_NAME);

        assertEquals(2, blobs);

        blobs = blobStore.countBlobs(GoogleFixtures.NONEXISTENT_BUCKET_NAME);

        assertEquals(0, blobs);
    }

    @Test
    public void getBlobIfContainerAndBlobExists() throws Exception {
        // TEXT and IMAGE file upload
        Payload pl = new StringPayload(GoogleFixtures.TEST_STRING_CONTENT);
        blobStore.putBlob(GoogleFixtures.TEMP_BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_STRING, pl));

        boolean exists = blobStore.blobExists(GoogleFixtures.TEMP_BUCKET_NAME, GoogleFixtures.TEST_STRING);
        assertTrue(exists);

        pl = new FilePayload(new File(ClassLoader.getSystemResource(GoogleFixtures.TEST_IMAGE).toURI()));
        blobStore.putBlob(GoogleFixtures.TEMP_BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_IMAGE, pl));

        exists = blobStore.blobExists(GoogleFixtures.TEMP_BUCKET_NAME, GoogleFixtures.TEST_IMAGE);
        assertTrue(exists);

        // DOWNLOAD
        Blob blob1 = blobStore.getBlob(GoogleFixtures.TEMP_BUCKET_NAME, GoogleFixtures.TEST_STRING);
        Blob blob2 = blobStore.getBlob(GoogleFixtures.TEMP_BUCKET_NAME, GoogleFixtures.TEST_IMAGE);

        assertNotNull(blob1);
        assertNotNull(blob2);
        assertNotNull(blob1.getPayload().getRawContent());
        assertNotNull(blob2.getPayload().getRawContent());
        assertEquals(8, blob1.getPayload().getContentLength());
        assertEquals(39482, blob2.getPayload().getContentLength());
    }

    @Test
    public void getBlobIfContainerExistsButNotTheBlob() throws Exception {
        Blob blob1 = blobStore.getBlob(GoogleFixtures.EXISTING_EMPTY_BUCKET_NAME, GoogleFixtures.TEST_STRING);

        assertNull(blob1);
    }

    @Test
    public void getBlobIfNorTheContainerNorTheBloExists() throws Exception {
        Blob blob1 = blobStore.getBlob(GoogleFixtures.NONEXISTENT_BUCKET_NAME, GoogleFixtures.TEST_STRING);

        assertNull(blob1);
    }

    @Test
    public void deleteBlobIfContainerAndBlobExists() throws Exception {
        boolean exists = blobStore.blobExists(GoogleFixtures.TEMP_BUCKET_NAME, GoogleFixtures.TEST_IMAGE);

        if (!exists) {
            Payload pl = new FilePayload(new File(ClassLoader.getSystemResource(GoogleFixtures.TEST_IMAGE).toURI()));
            boolean success = blobStore.putBlob(GoogleFixtures.TEMP_BUCKET_NAME, new DefaultBlob(GoogleFixtures.TEST_IMAGE, pl));
            assertTrue(success);
        }

        blobStore.removeBlob(GoogleFixtures.TEMP_BUCKET_NAME, GoogleFixtures.TEST_IMAGE);

        exists = blobStore.blobExists(GoogleFixtures.TEMP_BUCKET_NAME, GoogleFixtures.TEST_IMAGE);
        assertFalse(exists);
    }

    @Test
    public void deleteBlobIfContainerExistsButNotTheBlob() throws Exception {
        blobStore.removeBlob(GoogleFixtures.TEMP_BUCKET_NAME, GoogleFixtures.TEST_IMAGE);
    }

    @Test
    public void deleteBlobIfNorTheContainerNorTheBloExists() throws Exception {
        blobStore.removeBlob(GoogleFixtures.NONEXISTENT_BUCKET_NAME, GoogleFixtures.TEST_STRING);
    }
}
