package net.talqum.crossclouds.providers.google.vm;

import net.talqum.crossclouds.compute.Instance;
import net.talqum.crossclouds.compute.InstanceState;
import net.talqum.crossclouds.compute.common.ComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.providers.CloudContext;
import net.talqum.crossclouds.providers.google.fixtures.GoogleFixtures;
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
                .fromProvider("google")
                .addCredentials()
                .keyBased()
                    .accountId(GoogleFixtures.SERVICE_ACC_ID)
                    .projectId(GoogleFixtures.APP_ID)
                    .keyPath(GoogleFixtures.GOOGLE_CREDENTIALS)
                .location(GoogleFixtures.ZONE)
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
        Instance instance = computeCloud.getInstance(GoogleFixtures.RUNNING_INSTANCE_ID);

        assertNotNull(instance);
        assertEquals(GoogleFixtures.RUNNING_INSTANCE_ID, instance.getId());

        // region + availability zone
        assertEquals(GoogleFixtures.ZONE, instance.getZone());

        assertEquals(GoogleFixtures.HW_ID, instance.getHwConfigId());
        assertEquals(InstanceState.RUNNING, instance.getState());
    }

    @Test
    public void getInstanceWithIdFailNotFound() {
        exception.expect(ClientException.class);

        computeCloud.getInstance(GoogleFixtures.NONEXISTENT_INSTANCE_ID);
    }

    @Test
    public void getInstancesWithIdsFailNotFound() {
        exception.expect(ClientException.class);

        List<String> instanceIDs = new ArrayList<>(2);
        instanceIDs.add(GoogleFixtures.NONEXISTENT_INSTANCE_ID);
        instanceIDs.add(GoogleFixtures.RUNNING_INSTANCE_ID);

        computeCloud.getInstance(GoogleFixtures.NONEXISTENT_INSTANCE_ID);
    }
}
