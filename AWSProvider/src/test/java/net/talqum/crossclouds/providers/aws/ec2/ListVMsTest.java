package net.talqum.crossclouds.providers.aws.ec2;

import net.talqum.crossclouds.compute.Instance;
import net.talqum.crossclouds.compute.InstanceState;
import net.talqum.crossclouds.compute.common.ComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.providers.CloudContext;
import net.talqum.crossclouds.providers.aws.fixtures.AWSFixtures;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ListVMsTest {

    private static ComputeCloudContext ctx;
    private static ComputeCloud computeCloud;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setCtx() {
        ctx = CloudContext.create(ComputeCloudContext.class)
                .fromProvider("aws")
                .addCredentials()
                    .idAndSecretBased()
                        .id(AWSFixtures.ACC_ID)
                        .secret(AWSFixtures.ACC_SECRET)
                .build();

        computeCloud = ctx.getComputeCloud();
    }

    @Test
    public void listInstancesOk() {
        List<Instance> instances = computeCloud.listInstances();

        assertTrue(instances.size() == 1);
    }


    @Test
    public void getInstanceWithIdOK() {
        Instance instance = computeCloud.getInstance(AWSFixtures.RUNNING_INSTANCE_ID);

        assertNotNull(instance);
        assertEquals(AWSFixtures.RUNNING_INSTANCE_ID, instance.getId());

        // region + availability zone
        assertEquals(AWSFixtures.ZONE, instance.getZone() + "a");

        assertEquals(AWSFixtures.T2_MICRO, instance.getHwConfigId());
        assertEquals(InstanceState.RUNNING, instance.getState());
        assertEquals(AWSFixtures.UBUNTU_AMI, instance.getImageId());
    }

    @Test
    public void getInstanceWithIdFailNotFound() {
        exception.expect(ClientException.class);

        computeCloud.getInstance(AWSFixtures.NONEXISTENT_INSTANCE_ID);
    }

    @Test
    public void getInstancesWithIdsFailNotFound() {
        exception.expect(ClientException.class);

        List<String> instanceIDs = new ArrayList<>(2);
        instanceIDs.add(AWSFixtures.NONEXISTENT_INSTANCE_ID);
        instanceIDs.add(AWSFixtures.RUNNING_INSTANCE_ID);

        computeCloud.getInstance(AWSFixtures.NONEXISTENT_INSTANCE_ID);
    }

}
