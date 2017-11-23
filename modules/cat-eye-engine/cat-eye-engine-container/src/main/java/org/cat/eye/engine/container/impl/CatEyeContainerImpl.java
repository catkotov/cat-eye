package org.cat.eye.engine.container.impl;

import org.cat.eye.engine.common.CatEyeContainer;
import org.cat.eye.engine.common.CatEyeContainerRole;
import org.cat.eye.engine.common.CatEyeContainerState;
import org.cat.eye.engine.common.CatEyeContainerTaskCapacity;
import org.cat.eye.engine.common.crusher.ComputationExecutionTask;
import org.cat.eye.engine.container.datagram.DatagramReceiver;
import org.cat.eye.engine.container.datagram.DatagramSender;
import org.cat.eye.engine.common.deployment.BundleDeployer;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.container.discovery.*;
import org.cat.eye.engine.container.discovery.gossip.GossipContainerState;
import org.cat.eye.engine.container.discovery.gossip.GossipMessageProcessor;
import org.cat.eye.engine.container.discovery.gossip.GossipNeighboursState;
import org.cat.eye.engine.container.discovery.gossip.Heartbeat;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.container.service.ComputationContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import javax.annotation.PostConstruct;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CatEyeContainerImpl implements CatEyeContainer {

    private final static Logger LOGGER = LoggerFactory.getLogger(CatEyeContainerImpl.class);

    private NeighboursDiscoveryLeadingLight leadingLight;

    private DatagramSender datagramSender;

    private NeighboursDiscoveryReceiver neighbourDiscoveryReceiver;

    private DatagramReceiver datagramReceiver;
    // container name
    private String name;

    private GossipContainerState containerState;

    private GossipNeighboursState neighboursState;

    private Random random = new Random();

    private BundleManager bundleManager;

    private BundleDeployer bundleDeployer;

    private String pathToBundleJar;
    // TODO must get from out side service
    private String bundleDomain;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private final static int DEFAULT_COMPUTATION_THREAD_POOL_SIZE = 4;
    private AtomicInteger computationThreadPoolSize = new AtomicInteger(DEFAULT_COMPUTATION_THREAD_POOL_SIZE);
    private ExecutorService computationExecutorService;
    private final static String COMPUTATION_THREAD_NAME_PREFIX = "COMPUTATION-THREAD-";

    private Thread computeThread;

    private long computationThreadSleepTime = 100L;

    private final CatEyeContainerTaskCapacity containerTaskCapacity =
            new CatEyeContainerTaskCapacity(this.computationThreadPoolSize.get());

    private ComputationContextService computationContextService;

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
        // start computation work flow
        initComputationProcess();
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

    private void initComputationProcess() {
        isRunning.set(true);
        initComputationThreadPool();

        initComputeThread();

    }

    private void initComputeThread() {
        if (computeThread == null) {
            computeThread = new Thread(this::computeLoop, "ComputeThread");
        }

        if (!computeThread.isAlive()) {
            computeThread.start();
            LOGGER.info("CatEyeContainerImpl.initComputeThread - compute thread was started.");
        } else {
            LOGGER.info("CatEyeContainerImpl.initComputeThread - compute thread is already running.");
        }
    }

    private void computeLoop() {
        LOGGER.info("CatEyeContainerImpl.computeLoop - start the compute loop.");
        while (isRunning.get()) {
            containerTaskCapacity.await();
            // create and submit task
            createComputationExecutionTask();
            // sleep if container task capacity is exhausted
            if (containerTaskCapacity.getRemaining() <= 0) {
                try {
                    Thread.sleep(computationThreadSleepTime);
                } catch (InterruptedException e) {
                    LOGGER.error("CatEyeContainerImpl.computeLoop - exception during computation thread sleeping.", e);
                }
            }
        }
        LOGGER.info("CatEyeContainerImpl.computeLoop - finish the compute loop.");
    }

    private void createComputationExecutionTask() {

        int limit = containerTaskCapacity.getRemaining();
        // get computation list by service
        List<Computation> computations = computationContextService.takeComputationsForExecution(limit);
        // for every computation create and submit execution task
        if (computations != null && !computations.isEmpty()) {
            computations.forEach(c -> {
                ComputationExecutionTask task =
                        new ComputationExecutionTask(c, bundleManager.getBundle(c.getDomain()), computationContextService, containerTaskCapacity);
                computationExecutorService.submit(task);
            });
        }
    }

    private void initComputationThreadPool() {

        if (computationThreadPoolSize.get() > 0) {
            computationExecutorService = Executors.newFixedThreadPool(
                    computationThreadPoolSize.get(),
                    new CustomizableThreadFactory(COMPUTATION_THREAD_NAME_PREFIX)
            );
            containerTaskCapacity.setTotalTaskLimit(computationThreadPoolSize.get());
        } else {
            computationExecutorService =
                    Executors.newSingleThreadExecutor(new CustomizableThreadFactory(COMPUTATION_THREAD_NAME_PREFIX));
            containerTaskCapacity.setTotalTaskLimit(1);
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

    public void setComputationThreadPoolSize(Integer computationThreadPoolSize) {
        this.computationThreadPoolSize.set(computationThreadPoolSize);
    }

    public void setComputationThreadSleepTime(long computationThreadSleepTime) {
        this.computationThreadSleepTime = computationThreadSleepTime;
    }

    public void setComputationContextService(ComputationContextService computationContextService) {
        this.computationContextService = computationContextService;
    }

    public  ComputationContextService getComputationContextService() {
        return this.computationContextService;
    }

}
