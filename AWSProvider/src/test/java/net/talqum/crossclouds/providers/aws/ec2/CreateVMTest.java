package net.talqum.crossclouds.providers.aws.ec2;

import net.talqum.crossclouds.compute.vm.CreateRequest2;
import net.talqum.crossclouds.providers.aws.ec2.util.EC2InstanceCreateRequest;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 08. 09.
 * Time: 18:46
 */
public class CreateVMTest {

    CreateRequest2 cr = new EC2InstanceCreateRequest();
    cr.setName("alma");
}
