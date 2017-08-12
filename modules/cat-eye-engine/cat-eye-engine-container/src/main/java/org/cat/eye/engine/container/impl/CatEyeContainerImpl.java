package org.cat.eye.engine.container.impl;

import org.cat.eye.engine.container.CatEyeContainer;
import org.cat.eye.engine.container.discovery.NeighboursDiscoveryLeadingLight;
import org.cat.eye.engine.container.discovery.NeighboursDiscoveryReceiver;
import sun.java2d.loops.GraphicsPrimitive;

import javax.annotation.PostConstruct;
import java.net.NetworkInterface;
import java.util.List;

public class CatEyeContainerImpl implements CatEyeContainer {

    public String getName() {
        return null;
    }

    public int getThreadPoolSize() {
        return 0;
    }

    public void setThreadPoolSize(int size) {

    }

    @PostConstruct
    private void initialize() {
        // start discovery process
        startDiscovery();
        // deploy bundle

        // start calculation work flow
    }

    private void startDiscovery() {
        // find multicast network interface

        // start discovery process on interface
        Thread signalReceiver = new Thread(new NeighboursDiscoveryReceiver());
        signalReceiver.start();

        Thread signalSender = new Thread(new NeighboursDiscoveryLeadingLight());
        signalSender.start();

    }
}
