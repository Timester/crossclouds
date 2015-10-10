package net.talqum.crossclouds.providers.factory;

import net.talqum.crossclouds.common.Context;

public interface End {
    <C extends Context> C build();
}
