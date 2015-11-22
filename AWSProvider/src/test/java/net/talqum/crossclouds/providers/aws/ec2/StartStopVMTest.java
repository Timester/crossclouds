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

import static org.junit.Assert.assertEquals;

public class StartStopVMTest {
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
    public void stopInstanceOk() {
        Instance i = new Instance.Builder(AWSFixtures.RUNNING_INSTANCE_ID).build();

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

        Instance i = new Instance.Builder(AWSFixtures.NONEXISTENT_INSTANCE_ID).build();

        computeCloud.stopInstance(i);
    }

    @Test
    public void startInstanceOk() {
        Instance i = new Instance.Builder(AWSFixtures.RUNNING_INSTANCE_ID).build();

        Instance instance = computeCloud.getInstance(i.getId());
        assertEquals(InstanceState.STOPPED, instance.getState());

        computeCloud.startInstance(i);

        // we expect PROVISIONING, because it takes some time to get the instance running
        instance = computeCloud.getInstance(i.getId());
        assertEquals(InstanceState.PROVISIONING, instance.getState());
    }

    @Test
    public void startInstanceNotFound() {
        exception.expect(ClientException.class);

        Instance i = new Instance.Builder(AWSFixtures.NONEXISTENT_INSTANCE_ID).build();

        computeCloud.stopInstance(i);
    }
}
