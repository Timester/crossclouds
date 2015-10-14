package net.talqum.crossclouds.providers.aws.ec2;

import net.talqum.crossclouds.compute.common.ComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.compute.node.*;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.providers.CloudContext;
import net.talqum.crossclouds.providers.aws.fixtures.AWSFixtures;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

    private static Image image;
    private static Hardware hardware;
    private static Options options;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setCtx() {
        image = new Image.Builder(AWSFixtures.UBUNTU_AMI)
                .credentials(AWSFixtures.KEYPAIR_NAME)
                .build();

        hardware = new Hardware.Builder(AWSFixtures.T2_MICRO)
                .build();

        options = new Options.Builder()
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

    @Ignore
    @Test
    public void createVMOK() {
        Template template = new Template.Builder("teszt")
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
        Template tmp = new Template.Builder("teszt")
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
        Hardware hw = new Hardware.Builder("")
                .build();

        Template tmp = new Template.Builder("teszt")
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
        Image di = new Image.Builder("os1")
                .credentials("cred1")
                .build();

        Template tmp = new Template.Builder("teszt")
                .hardware(hardware)
                .image(di)
                .options(options)
                .build();

        exception.expect(ClientException.class);

        computeCloud.createAndStartInstance(tmp);
    }

    @Test
    public void createVMFailBecauseOfInvalidParameters2() {
        Image di = new Image.Builder(AWSFixtures.UBUNTU_AMI)
                .credentials("cred1")
                .build();

        Template tmp = new Template.Builder("teszt")
                .hardware(hardware)
                .image(di)
                .options(options)
                .build();

        exception.expect(ClientException.class);

        computeCloud.createAndStartInstance(tmp);
    }

    @Test
    public void createVMFailBecauseOfInvalidParameters3() {
        Hardware hw = new Hardware.Builder("valami").build();

        Template tmp = new Template.Builder("teszt")
                .hardware(hw)
                .image(image)
                .options(options)
                .build();

        exception.expect(ClientException.class);

        computeCloud.createAndStartInstance(tmp);
    }

    @Test
    public void createVMFailBecauseOfInvalidParameters4() {
        Options opt = new Options.Builder()
                .maxInstancesCount(1)
                .minInstancesCount(1)
                .securityGroup("secgrp")
                .build();

        Template tmp = new Template.Builder("teszt")
                .hardware(hardware)
                .image(image)
                .options(opt)
                .build();

        exception.expect(ClientException.class);

        computeCloud.createAndStartInstance(tmp);
    }
}
