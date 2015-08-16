package net.talqum.crossclouds.providers.aws.ec2.util;

import net.talqum.crossclouds.compute.vm.CreateRequest2;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015. 08. 09.
 * Time: 18:45
 */
public class EC2InstanceCreateRequest extends CreateRequest2 {
    private String keyName;

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
