package net.talqum.crossclouds.providers.aws.s3;

import net.talqum.crossclouds.blobstorage.common.*;
import net.talqum.crossclouds.blobstorage.payloads.FilePayload;
import net.talqum.crossclouds.blobstorage.payloads.StringPayload;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.providers.ContextFactory;
import net.talqum.crossclouds.providers.aws.fixtures.AWSFixtures;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Imre on 2015.03.25..
 */
public class AWSS3BlobStoreAcceptanceTest {
    private static BlobStoreContext ctx;
    private static BlobStore blobStore;

    @BeforeClass
    public static void setCtx() {
        ctx = ContextFactory
                .newFactory("aws")
                .credentials(AWSFixtures.ACC_ID, AWSFixtures.ACC_SECRET)
                .build(BlobStoreContext.class);

        blobStore = ctx.getBlobStore();
    }

    @Before
    public void init() {
        try {
            blobStore.deleteContainer(AWSFixtures.TEMP_BUCKET_NAME + 1);

            blobStore.createContainer(AWSFixtures.TEMP_BUCKET_NAME);
            blobStore.clearContainer(AWSFixtures.TEMP_BUCKET_NAME);
        } catch (ClientException e) {
            System.err.println("Error occured in the init section. No further action needed.");
        }
    }

    @Test
    public void containerExists() throws Exception {
        assertFalse(blobStore.containerExists(AWSFixtures.NONEXISTENT_BUCKET_NAME));
        assertTrue(blobStore.containerExists(AWSFixtures.EXISTING_NONEMPTY_BUCKET_NAME));
    }

    @Test
    public void createContainerIfpreviouslyWasNonexistent() throws Exception {
        assertFalse(blobStore.containerExists(AWSFixtures.TEMP_BUCKET_NAME + 1));
        assertTrue(blobStore.createContainer(AWSFixtures.TEMP_BUCKET_NAME + 1));
        assertTrue(blobStore.containerExists(AWSFixtures.TEMP_BUCKET_NAME + 1));
    }

    @Test
    public void createContainerIfpreviouslyWasExistent() throws Exception {
        assertTrue(blobStore.containerExists(AWSFixtures.EXISTING_NONEMPTY_BUCKET_NAME));
        assertFalse(blobStore.createContainer(AWSFixtures.EXISTING_NONEMPTY_BUCKET_NAME));
    }

    @Test
    public void listContainerContentIfExistsAndNotEmpty() throws Exception {
        Set<String> content = blobStore.listContainerContent(AWSFixtures.EXISTING_NONEMPTY_BUCKET_NAME);

        assertNotNull(content);
        assertEquals(2, content.size());
        assertTrue(content.contains(AWSFixtures.TEST_IMAGE));
        assertTrue(content.contains(AWSFixtures.TEST_STRING));
    }

    @Test
    public void listContainerContentIfExistsAndEmpty() throws Exception {
        Set<String> content = blobStore.listContainerContent(AWSFixtures.EXISTING_EMPTY_BUCKET_NAME);

        assertNotNull(content);
        assertEquals(0, content.size());
    }

    @Test
    public void listContainerContentIfNotExists() throws Exception {
        Set<String> content = blobStore.listContainerContent(AWSFixtures.NONEXISTENT_BUCKET_NAME);

        assertNotNull(content);
        assertEquals(0, content.size());
    }

    @Test
    public void clearContainerIfExists() throws Exception {
        Payload pl = new StringPayload(AWSFixtures.TEST_STRING_CONTENT);
        blobStore.putBlob(AWSFixtures.TEMP_BUCKET_NAME, new DefaultBlob(AWSFixtures.TEST_STRING, pl));

        assertEquals(1, blobStore.countBlobs(AWSFixtures.TEMP_BUCKET_NAME));

        blobStore.clearContainer(AWSFixtures.TEMP_BUCKET_NAME);

        assertEquals(0, blobStore.countBlobs(AWSFixtures.TEMP_BUCKET_NAME));
    }

    @Test
    public void clearContainerIfNotExists() throws Exception {
        blobStore.clearContainer(AWSFixtures.NONEXISTENT_BUCKET_NAME);
    }

    @Test
    public void deleteContainerIfExists() throws Exception {
        assertTrue(blobStore.containerExists(AWSFixtures.TEMP_BUCKET_NAME));
        blobStore.deleteContainer(AWSFixtures.TEMP_BUCKET_NAME);
        assertFalse(blobStore.containerExists(AWSFixtures.TEMP_BUCKET_NAME));
    }

    @Test
    public void deleteContainerIfNotExists() throws Exception {
        blobStore.deleteContainer(AWSFixtures.NONEXISTENT_BUCKET_NAME);
    }

    @Test
    public void putFileBlob() throws Exception {
        Payload pl;
        pl = new FilePayload(new File(ClassLoader.getSystemResource(AWSFixtures.TEST_IMAGE).toURI()));

        boolean success = blobStore.putBlob(AWSFixtures.TEMP_BUCKET_NAME, new DefaultBlob(AWSFixtures.TEST_IMAGE, pl));

        assertTrue(success);
    }

    @Test
    public void putStringBlob() throws Exception {
        Payload pl = new StringPayload(AWSFixtures.TEST_STRING_CONTENT);

        boolean success = blobStore.putBlob(AWSFixtures.TEMP_BUCKET_NAME, new DefaultBlob(AWSFixtures.TEST_STRING, pl));

        assertTrue(success);
    }

    @Test
    public void blobExist() throws Exception {
        boolean exists = blobStore.blobExists(AWSFixtures.TEMP_BUCKET_NAME, AWSFixtures.TEST_STRING);
        assertFalse(exists);

        Payload pl = new StringPayload(AWSFixtures.TEST_STRING_CONTENT);

        blobStore.putBlob(AWSFixtures.TEMP_BUCKET_NAME, new DefaultBlob(AWSFixtures.TEST_STRING, pl));

        exists = blobStore.blobExists(AWSFixtures.TEMP_BUCKET_NAME, AWSFixtures.TEST_STRING);
        assertTrue(exists);

        blobStore.removeBlob(AWSFixtures.TEMP_BUCKET_NAME, AWSFixtures.TEST_STRING);

        exists = blobStore.blobExists(AWSFixtures.TEMP_BUCKET_NAME, AWSFixtures.TEST_STRING);
        assertFalse(exists);
    }

    @Test
    public void countBlobs() throws Exception {
        long blobs = blobStore.countBlobs(AWSFixtures.EXISTING_NONEMPTY_BUCKET_NAME);

        assertEquals(2, blobs);

        blobs = blobStore.countBlobs(AWSFixtures.NONEXISTENT_BUCKET_NAME);

        assertEquals(0, blobs);
    }

    @Test
    public void getBlobIfContainerAndBlobExists() throws Exception {
        // TEXT and IMAGE file upload
        Payload pl = new StringPayload(AWSFixtures.TEST_STRING_CONTENT);
        blobStore.putBlob(AWSFixtures.TEMP_BUCKET_NAME, new DefaultBlob(AWSFixtures.TEST_STRING, pl));

        boolean exists = blobStore.blobExists(AWSFixtures.TEMP_BUCKET_NAME, AWSFixtures.TEST_STRING);
        assertTrue(exists);

        pl = new FilePayload(new File(ClassLoader.getSystemResource(AWSFixtures.TEST_IMAGE).toURI()));
        blobStore.putBlob(AWSFixtures.TEMP_BUCKET_NAME, new DefaultBlob(AWSFixtures.TEST_IMAGE, pl));

        exists = blobStore.blobExists(AWSFixtures.TEMP_BUCKET_NAME, AWSFixtures.TEST_IMAGE);
        assertTrue(exists);

        // DOWNLOAD
        Blob blob1 = blobStore.getBlob(AWSFixtures.TEMP_BUCKET_NAME, AWSFixtures.TEST_STRING);
        Blob blob2 = blobStore.getBlob(AWSFixtures.TEMP_BUCKET_NAME, AWSFixtures.TEST_IMAGE);

        assertNotNull(blob1);
        assertNotNull(blob2);
        assertNotNull(blob1.getPayload().getRawContent());
        assertNotNull(blob2.getPayload().getRawContent());
        assertEquals(8, blob1.getPayload().getContentLength());
        assertEquals(39482, blob2.getPayload().getContentLength());
    }

    @Test
    public void getBlobIfContainerExistsButNotTheBlob() throws Exception {
        Blob blob1 = blobStore.getBlob(AWSFixtures.EXISTING_EMPTY_BUCKET_NAME, AWSFixtures.TEST_STRING);

        assertNull(blob1);
    }

    @Test
    public void getBlobIfNorTheContainerNorTheBloExists() throws Exception {
        Blob blob1 = blobStore.getBlob(AWSFixtures.NONEXISTENT_BUCKET_NAME, AWSFixtures.TEST_STRING);

        assertNull(blob1);
    }

    @Test
    public void deleteBlobIfContainerAndBlobExists() throws Exception {
        boolean exists = blobStore.blobExists(AWSFixtures.TEMP_BUCKET_NAME, AWSFixtures.TEST_IMAGE);

        if (!exists) {
            Payload pl = new FilePayload(new File(ClassLoader.getSystemResource(AWSFixtures.TEST_IMAGE).toURI()));
            boolean success = blobStore.putBlob(AWSFixtures.TEMP_BUCKET_NAME, new DefaultBlob(AWSFixtures.TEST_IMAGE, pl));
            assertTrue(success);
        }

        blobStore.removeBlob(AWSFixtures.TEMP_BUCKET_NAME, AWSFixtures.TEST_IMAGE);

        exists = blobStore.blobExists(AWSFixtures.TEMP_BUCKET_NAME, AWSFixtures.TEST_IMAGE);
        assertFalse(exists);
    }

    @Test
    public void deleteBlobIfContainerExistsButNotTheBlob() throws Exception {
        blobStore.removeBlob(AWSFixtures.TEMP_BUCKET_NAME, AWSFixtures.TEST_IMAGE);
    }

    @Test
    public void deleteBlobIfNorTheContainerNorTheBloExists() throws Exception {
        blobStore.removeBlob(AWSFixtures.NONEXISTENT_BUCKET_NAME, AWSFixtures.TEST_STRING);
    }
}
