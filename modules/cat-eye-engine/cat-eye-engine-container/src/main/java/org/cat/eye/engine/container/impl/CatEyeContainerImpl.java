package org.cat.eye.engine.container.impl;

import org.cat.eye.engine.container.CatEyeContainer;
import org.cat.eye.engine.container.discovery.DatagramReceiver;
import org.cat.eye.engine.container.discovery.DatagramSender;
import org.cat.eye.engine.container.discovery.NeighboursDiscoveryLeadingLight;
import org.cat.eye.engine.container.discovery.NeighboursDiscoveryReceiver;
import javax.annotation.PostConstruct;

public class CatEyeContainerImpl implements CatEyeContainer {

    private NeighboursDiscoveryLeadingLight leadingLight;

    private DatagramSender datagramSender;

    private NeighboursDiscoveryReceiver neighbourDiscoveryReceiver;

    private DatagramReceiver datagramReceiver;

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
        Thread signalReceiverThread = new Thread(this.neighbourDiscoveryReceiver);
        signalReceiverThread.start();

        Thread datagramReceiverThread = new Thread(this.datagramReceiver);
        datagramReceiverThread.start();

        Thread datagramSenderThread = new Thread(this.datagramSender);
        datagramSenderThread.start();

        Thread signalSenderThread = new Thread(this.leadingLight);
        signalSenderThread.start();

    }

    public void setLeadingLight(NeighboursDiscoveryLeadingLight leadingLight) {
        this.leadingLight = leadingLight;
    }

    public void setDatagramSender(DatagramSender datagramSender) {
        this.datagramSender = datagramSender;
    }

    public void setDatagramReceiver(DatagramReceiver datagramReceiver) {
        this.datagramReceiver = datagramReceiver;
    }

    public void setNeighbourDiscoveryReceiver(NeighboursDiscoveryReceiver neighbourDiscoveryReceiver) {
        this.neighbourDiscoveryReceiver = neighbourDiscoveryReceiver;
    }
}
