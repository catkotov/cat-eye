package org.cat.eye.engine.container.impl;

import org.cat.eye.engine.container.CatEyeContainer;
import org.cat.eye.engine.container.CatEyeContainerRole;
import org.cat.eye.engine.container.CatEyeContainerState;
import org.cat.eye.engine.container.datagram.DatagramReceiver;
import org.cat.eye.engine.container.datagram.DatagramSender;
import org.cat.eye.engine.container.deployment.BundleDeployer;
import org.cat.eye.engine.container.deployment.management.BundleManager;
import org.cat.eye.engine.container.discovery.*;
import org.cat.eye.engine.container.discovery.gossip.GossipContainerState;
import org.cat.eye.engine.container.discovery.gossip.GossipMessageProcessor;
import org.cat.eye.engine.container.discovery.gossip.GossipNeighboursState;
import org.cat.eye.engine.container.discovery.gossip.Heartbeat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;
import java.util.Random;

public class CatEyeContainerImpl implements CatEyeContainer {

    private final static Logger LOGGER = LoggerFactory.getLogger(CatEyeContainerImpl.class);

    private NeighboursDiscoveryLeadingLight leadingLight;

    private DatagramSender datagramSender;

    private NeighboursDiscoveryReceiver neighbourDiscoveryReceiver;

    private DatagramReceiver datagramReceiver;

    private String name;

    private GossipContainerState containerState;

    private GossipNeighboursState neighboursState;

    private Random random = new Random();

    private BundleManager bundleManager;

    private BundleDeployer bundleDeployer;

    private String pathToBundleJar;

    private String bundleDomain;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThreadPoolSize() {
        return 0;
    }

    public void setThreadPoolSize(int size) {

    }

    @PostConstruct
    private void initialize() throws SocketException {
        //
        containerStateInitialize();
        // start discovery process
        startDiscovery();
        // deploy bundle
        bundleDeployer.deploy(pathToBundleJar, bundleDomain);
        // start calculation work flow

    }

    private void containerStateInitialize() {

        containerState = new GossipContainerState();
        containerState.setContainerName(getName());
        containerState.setContainerRole(CatEyeContainerRole.UNDEFINED);
        containerState.setContainerState(CatEyeContainerState.STARTED);
        containerState.setContainerHeartbeat(new Heartbeat(1L, random.nextLong()));

        neighboursState = new GossipNeighboursState();

        leadingLight.setContainerState(containerState);
        leadingLight.setNeighboursState(neighboursState);
    }

    private void startDiscovery() throws SocketException {
        // find multicast network interface
        MulticastNetworkInterfaceFinder interfaceFinder = new MulticastNetworkInterfaceFinder();
        List<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = interfaceFinder.getMulticastInterfaces();
        } catch (SocketException e) {
            LOGGER.error("startDiscovery - " + e.getMessage());
            throw e;
        }

        if (networkInterfaces != null && !networkInterfaces.isEmpty()) {

            this.datagramReceiver.setNetworkInterfaceName(networkInterfaces.get(0).getName());
            this.datagramSender.setNetworkInterfaceName(networkInterfaces.get(0).getName());

            // start processing discovery messages
            GossipMessageProcessor messageProcessor = new GossipMessageProcessor(containerState, neighboursState);
            neighbourDiscoveryReceiver.setMessageProcessor(messageProcessor);
            Thread messageProcessorThread = new Thread(messageProcessor);
            messageProcessorThread.start();

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

    public void setBundleManager(BundleManager bundleManager) {
        this.bundleManager = bundleManager;
    }

    public void setBundleDeployer(BundleDeployer bundleDeployer) {
        this.bundleDeployer = bundleDeployer;
    }

    public void setPathToBundleJar(String pathToBundleJar) {
        this.pathToBundleJar = pathToBundleJar;
    }

    public void setBundleDomain(String bundleDomain) {
        this.bundleDomain = bundleDomain;
    }
}
