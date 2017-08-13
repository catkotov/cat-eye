package org.cat.eye.engine.container.impl;

import org.cat.eye.engine.container.CatEyeContainer;
import org.cat.eye.engine.container.discovery.*;

import javax.annotation.PostConstruct;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;

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
        MulticastNetworkInterfaceFinder interfaceFinder = new MulticastNetworkInterfaceFinder();
        List<NetworkInterface> networkInterfaces = null;
        try {
            networkInterfaces = interfaceFinder.getMulticastInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        if (networkInterfaces != null && !networkInterfaces.isEmpty()) {

            this.datagramReceiver.setNetworkInterfaceName(networkInterfaces.get(0).getName());
            this.datagramSender.setNetworkInterfaceName(networkInterfaces.get(0).getName());

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
