package org.cat.eye.engine.container.impl;

import org.cat.eye.engine.container.CatEyeContainer;

public class CatEyeContainerImpl implements CatEyeContainer {

    public String getName() {
        return null;
    }

    public int getThreadPoolSize() {
        return 0;
    }

    public void setThreadPoolSize(int size) {

    }

    private void initialize() {
        // start discovery process
        startDiscovery();
        // deploy bundle

        // start calculation work flow
    }

    private void startDiscovery() {
        // find multicast network interface

        // start discovery process on interface

    }
}
