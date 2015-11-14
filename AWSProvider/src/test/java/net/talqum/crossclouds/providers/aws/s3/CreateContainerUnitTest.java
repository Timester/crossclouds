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
        when(mockContext.getClient()).thenReturn(client);
    }

    @Test
    public void createContainerSuccessAlreadyExists(){
        when(client.doesBucketExist(AWSFixtures.TEMP_BUCKET_NAME)).thenReturn(true);

        try {
            boolean response = blobStore.createContainer(AWSFixtures.TEMP_BUCKET_NAME);
            assertFalse(response);
        } catch (ClientException e) {
            fail();
        }

        verify(client).doesBucketExist(AWSFixtures.TEMP_BUCKET_NAME);
        verifyNoMoreInteractions(client);
    }

    @Test
    public void createContainerSuccessNotExistsYet(){
        when(client.doesBucketExist(AWSFixtures.TEMP_BUCKET_NAME)).thenReturn(false);
        when(client.createBucket(any(CreateBucketRequest.class))).thenReturn(new Bucket());

        try {
            boolean response = blobStore.createContainer(AWSFixtures.TEMP_BUCKET_NAME);
            assertTrue(response);
        } catch (ClientException e) {
            fail();
        }

        verify(client).doesBucketExist(AWSFixtures.TEMP_BUCKET_NAME);
        verify(client).createBucket(any(CreateBucketRequest.class));
        verifyNoMoreInteractions(client);
    }

    @Test
    public void createContainerFailServiceFail(){
        AmazonServiceException ase = new AmazonServiceException("Error");
        ase.setErrorCode("");
        
        when(client.doesBucketExist(AWSFixtures.TEMP_BUCKET_NAME)).thenReturn(false);
        when(client.createBucket(any(CreateBucketRequest.class))).thenThrow(ase);

        try {
            blobStore.createContainer(AWSFixtures.TEMP_BUCKET_NAME);
        } catch (ClientException e) {
            assertTrue(e instanceof ProviderException);
            assertTrue(e.getErrorCode().equals(ClientErrorCodes.SERVICE_UNAVAILABLE));
        }

        verify(client).doesBucketExist(AWSFixtures.TEMP_BUCKET_NAME);
        verify(client).createBucket(any(CreateBucketRequest.class));
        verifyNoMoreInteractions(client);
    }

    @Test
    public void createContainerFailClientFail(){
        when(client.doesBucketExist(AWSFixtures.TEMP_BUCKET_NAME)).thenReturn(false);
        when(client.createBucket(any(CreateBucketRequest.class))).thenThrow(AmazonClientException.class);

        try {
            blobStore.createContainer(AWSFixtures.TEMP_BUCKET_NAME);
        } catch (Exception e) {
            assertTrue(e instanceof ClientException);
            assertTrue(((ClientException)e).getErrorCode().equals(ClientErrorCodes.NO_NETWORK));
        }

        verify(client).doesBucketExist(AWSFixtures.TEMP_BUCKET_NAME);
        verify(client).createBucket(any(CreateBucketRequest.class));
        verifyNoMoreInteractions(client);
    }
}