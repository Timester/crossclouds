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

import static org.junit.Assert.assertEquals;

public class StartStopVMTest {
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
    public void stopInstanceOk() {
        Instance i = new Instance.Builder(GoogleFixtures.RUNNING_INSTANCE_ID).build();

        Instance instance = computeCloud.getInstance(i.getId());
        assertEquals(InstanceState.RUNNING, instance.getState());

        computeCloud.stopInstance(i);

        // we expect STOPPING, because it takes some time to get the instance running
        instance = computeCloud.getInstance(i.getId());
        assertEquals(InstanceState.STOPPING, instance.getState());
    }

    @Test
    public void stopInstanceNotFound() {
        exception.expect(ClientException.class);

        Instance i = new Instance.Builder(GoogleFixtures.NONEXISTENT_INSTANCE_ID).build();

        computeCloud.stopInstance(i);
    }

    @Test
    public void startInstanceOk() {
        Instance i = new Instance.Builder(GoogleFixtures.RUNNING_INSTANCE_ID).build();

        Instance instance = computeCloud.getInstance(i.getId());
        assertEquals(InstanceState.STOPPED, instance.getState());

        computeCloud.startInstance(i);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        instance = computeCloud.getInstance(i.getId());
        assertEquals(InstanceState.STARTING, instance.getState());
    }

    @Test
    public void startInstanceNotFound() {
        exception.expect(ClientException.class);

        Instance i = new Instance.Builder(GoogleFixtures.NONEXISTENT_INSTANCE_ID).build();

        computeCloud.stopInstance(i);
    }
}
