package net.talqum.crossclouds.providers.google.vm;

import net.talqum.crossclouds.compute.common.ComputeCloud;
import net.talqum.crossclouds.compute.common.ComputeCloudContext;
import net.talqum.crossclouds.compute.node.Hardware;
import net.talqum.crossclouds.compute.node.Image;
import net.talqum.crossclouds.compute.node.Options;
import net.talqum.crossclouds.compute.node.Template;
import net.talqum.crossclouds.exceptions.ClientException;
import net.talqum.crossclouds.providers.CloudContext;
import net.talqum.crossclouds.providers.google.fixtures.GoogleFixtures;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CreateVmAcceptanceTest {
    private static ComputeCloudContext ctx;
    private static ComputeCloud computeCloud;

    private static Image image;
    private static Hardware hardware;
    private static Options options;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setCtx() {
        image = new Image.Builder(GoogleFixtures.IMAGE_ID)
                .build();

        hardware = new Hardware.Builder(GoogleFixtures.HW_ID)
                .build();

        options = new Options.Builder()
                .maxInstancesCount(1)
                .minInstancesCount(1)
                .location(GoogleFixtures.ZONE)
                .build();

        ctx = CloudContext.create(ComputeCloudContext.class)
                .fromProvider("google")
                .addCredentials()
                .keyBased()
                    .accountId(GoogleFixtures.SERVICE_ACC_ID)
                    .projectId(GoogleFixtures.APP_ID)
                    .keyPath(GoogleFixtures.GOOGLE_CREDENTIALS)
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
        Image di = new Image.Builder(GoogleFixtures.IMAGE_ID)
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
                .location(GoogleFixtures.ZONE)
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
