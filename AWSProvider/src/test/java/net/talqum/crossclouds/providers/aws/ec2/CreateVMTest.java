package net.talqum.crossclouds.providers.aws.ec2;

import net.talqum.crossclouds.compute.common.ComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.compute.node.*;
import net.talqum.crossclouds.providers.CloudContext;
import net.talqum.crossclouds.providers.aws.fixtures.AWSFixtures;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 08. 09.
 * Time: 18:46
 */
public class CreateVMTest {

    private static ComputeCloudContext ctx;
    private static ComputeCloud computeCloud;

    private static DefaultImage image;
    private static DefaultHardware hardware;
    private static DefaultOptions options;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setCtx() {
        image = new DefaultImage.Builder(AWSFixtures.UBUNTU_AMI)
                .credentials(AWSFixtures.KEYPAIR_NAME)
                .build();

        hardware = new DefaultHardware.Builder(AWSFixtures.T2_MICRO)
                .build();

        options = new DefaultOptions.Builder()
                .maxInstancesCount(1)
                .minInstancesCount(1)
                .securityGroup(AWSFixtures.SECURITY_GROUP_ID)
                .build();

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
    public void createVMOK() {
        Template template = new DefaultTemplate.Builder("teszt")
                .hardware(hardware)
                .image(image)
                .options(options)
                .build();

        computeCloud.createAndStartInstance(template);
    }

    @Test
    public void createVMFailBecauseOfInvalidTemplate() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid template parameters: template");

        computeCloud.createAndStartInstance(null);
    }

    @Test
    public void createVMFailBecauseOfInvalidTemplateImage() {
        Template tmp = new DefaultTemplate.Builder("teszt")
                .hardware(hardware)
                .image(null)
                .options(options)
                .build();

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid template parameters: image");

        computeCloud.createAndStartInstance(tmp);
    }

    @Test
    public void createVMFailBecauseOfInvalidTemplateHardwareAndOptions() {
        DefaultHardware hw = new DefaultHardware.Builder("")
                .build();

        Template tmp = new DefaultTemplate.Builder("teszt")
                .hardware(hw)
                .image(image)
                .options(null)
                .build();

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid template parameters: hardware/configId, options");

        computeCloud.createAndStartInstance(tmp);
    }

    @Test
    public void createVMFailBecauseOfInvalidParameters() {

    }
}
