package net.talqum.crossclouds.providers.google.vm;

import net.talqum.crossclouds.compute.Instance;
import net.talqum.crossclouds.compute.common.ComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.providers.CloudContext;
import net.talqum.crossclouds.providers.google.fixtures.GoogleFixtures;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
                .async(true)
                .location(GoogleFixtures.ZONE)
                .build();

        computeCloud = ctx.getComputeCloud();
    }

    @Test
    public void listInstancesOk() {
        List<Instance> instances = computeCloud.listInstances();

        assertTrue(instances.size() == 1);
    }
}
