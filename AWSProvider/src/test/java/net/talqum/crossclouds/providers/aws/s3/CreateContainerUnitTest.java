package net.talqum.crossclouds.providers.aws.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;

import net.talqum.crossclouds.exceptions.ClientErrorCodes;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.exceptions.ProviderException;
import net.talqum.crossclouds.providers.aws.fixtures.AWSFixtures;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015.05.15.
 * Time: 17:40
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateContainerUnitTest {

    @Mock
    private DefaultAWSS3BlobStoreContext mockContext;

    @Mock
    private AmazonS3Client client;

    @InjectMocks
    private AWSS3BlobStore blobStore;

    @Before
    public void init(){
        when(mockContext.getS3Client()).thenReturn(client);
    }

    @Test
    public void createContainerSuccessAlreadyExists(){
        when(client.doesBucketExist(AWSFixtures.BUCKET_NAME)).thenReturn(true);

        try {
            boolean response = blobStore.createContainer(AWSFixtures.BUCKET_NAME);
            assertFalse(response);
        } catch (ClientException e) {
            fail();
        }

        verify(client).doesBucketExist(AWSFixtures.BUCKET_NAME);
        verifyNoMoreInteractions(client);
    }

    @Test
    public void createContainerSuccessNotExistsYet(){
        when(client.doesBucketExist(AWSFixtures.BUCKET_NAME)).thenReturn(false);
        when(client.createBucket(any(CreateBucketRequest.class))).thenReturn(new Bucket());

        try {
            boolean response = blobStore.createContainer(AWSFixtures.BUCKET_NAME);
            assertTrue(response);
        } catch (ClientException e) {
            fail();
        }

        verify(client).doesBucketExist(AWSFixtures.BUCKET_NAME);
        verify(client).createBucket(any(CreateBucketRequest.class));
        verifyNoMoreInteractions(client);
    }

    @Test
    public void createContainerFailServiceFail(){
        when(client.doesBucketExist(AWSFixtures.BUCKET_NAME)).thenReturn(false);
        when(client.createBucket(any(CreateBucketRequest.class))).thenThrow(AmazonServiceException.class);

        try {
            blobStore.createContainer(AWSFixtures.BUCKET_NAME);
        } catch (ClientException e) {
            assertTrue(e instanceof ProviderException);
            assertTrue(e.getErrorCode().equals(ClientErrorCodes.SERVICE_UNAVAILABLE));
        }

        verify(client).doesBucketExist(AWSFixtures.BUCKET_NAME);
        verify(client).createBucket(any(CreateBucketRequest.class));
        verifyNoMoreInteractions(client);
    }

    @Test
    public void createContainerFailClientFail(){
        when(client.doesBucketExist(AWSFixtures.BUCKET_NAME)).thenReturn(false);
        when(client.createBucket(any(CreateBucketRequest.class))).thenThrow(AmazonClientException.class);

        try {
            blobStore.createContainer(AWSFixtures.BUCKET_NAME);
        } catch (ClientException e) {
            assertTrue(e instanceof ClientException);
            assertTrue(e.getErrorCode().equals(ClientErrorCodes.NO_NETWORK));
        }

        verify(client).doesBucketExist(AWSFixtures.BUCKET_NAME);
        verify(client).createBucket(any(CreateBucketRequest.class));
        verifyNoMoreInteractions(client);
    }
}