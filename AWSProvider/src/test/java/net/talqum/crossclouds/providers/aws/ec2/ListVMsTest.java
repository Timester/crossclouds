package net.talqum.crossclouds.providers.aws.ec2;

import net.talqum.crossclouds.compute.common.ComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.providers.CloudContext;
import net.talqum.crossclouds.providers.aws.fixtures.AWSFixtures;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
        computeCloud.listInstances();
    }
}
