package org.cat.eye.engine.container.unit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.PatternsCS;
import akka.util.Timeout;
import org.cat.eye.engine.common.CatEyeContainer;
import org.cat.eye.engine.common.CatEyeContainerTaskCapacity;
import org.cat.eye.engine.common.crusher.ComputationExecutionTask;
import org.cat.eye.engine.common.deployment.BundleDeployer;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.cat.eye.engine.common.service.impl.ComputationsQueueActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import scala.concurrent.duration.FiniteDuration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Kotov on 24.11.2017.
 */
public class CatEyeContainerUnit implements CatEyeContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatEyeContainerUnit.class);

    // container name
    private String name;

    private BundleDeployer bundleDeployer;

    private BundleManager bundleManager;

    private String pathToClasses;

    private String bundleDomain;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private final static int DEFAULT_COMPUTATION_THREAD_POOL_SIZE = 128;
    private AtomicInteger computationThreadPoolSize = new AtomicInteger(DEFAULT_COMPUTATION_THREAD_POOL_SIZE);
    private ExecutorService computationExecutorService;
    private final static String COMPUTATION_THREAD_NAME_PREFIX = "COMPUTATION-THREAD-";

    private final CatEyeContainerTaskCapacity containerTaskCapacity =
            new CatEyeContainerTaskCapacity(this.computationThreadPoolSize.get());

    private Thread computeThread;

    private long computationThreadSleepTime = 5L;

    private ComputationContextService computationContextService;

    private ActorSystem actorSystem;

    private ActorRef computationQueue;

    @Override
    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    @Override
    public int getThreadPoolSize() {
        return computationThreadPoolSize.get();
    }

    @Override
    public void setThreadPoolSize(int size) {
        computationThreadPoolSize.set(size);
    }

    public void initialize() throws Exception {
        // deploy bundle
        bundleDeployer.deploy(pathToClasses, bundleDomain);
        // start computation work flow
        initComputationProcess();
    }

    private void initComputationProcess() {
        isRunning.set(true);
        initComputationThreadPool();

        initComputeThread();

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

    private void initComputeThread() {
        if (computeThread == null) {
            computeThread = new Thread(this::computeLoop, "ComputeThread");
        }

        if (!computeThread.isAlive()) {
            LOGGER.info("initComputeThread - start compute thread.");
            computeThread.start();
            try {
                computeThread.join();
                computationExecutorService.shutdown();
            } catch (InterruptedException e) {
                LOGGER.info("initComputeThread - compute thread was interrupted.");
            }
            LOGGER.info("initComputeThread - compute thread was finished.");
        } else {
            LOGGER.info("initComputeThread - compute thread is already running.");
        }
    }

    private void computeLoop() {
        LOGGER.info("computeLoop - start the compute loop.");
        while (isRunning.get()) {
            containerTaskCapacity.await();
            // create and submit task
            isRunning.set(createComputationExecutionTask());
            // sleep if container task capacity is exhausted
            if (containerTaskCapacity.getRemaining() <= 0) {
                try {
                    Thread.sleep(computationThreadSleepTime);
                } catch (InterruptedException e) {
                    LOGGER.error("computeLoop - exception during computation thread sleeping.", e);
                }
            }
        }
        LOGGER.info("computeLoop - finish the compute loop.");

        actorSystem.terminate();
        actorSystem.getWhenTerminated().toCompletableFuture().join();

        LOGGER.info("computeLoop - Actor System was terminated.");
    }

    @SuppressWarnings("unchecked")
    private boolean createComputationExecutionTask() {

        int limit = containerTaskCapacity.getRemaining();
        // get computation list by service
        ComputationsQueueActor.TakeComputations takeComputations = new ComputationsQueueActor.TakeComputations(limit);
        FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);

        List<Computation> computations = null;
        try {
            computations = (List<Computation>)
                    PatternsCS.ask(computationQueue, takeComputations, timeout).toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO
            e.printStackTrace();
        }

        // for every computation create and submit execution task
        if (computations != null && !computations.isEmpty()) {

            List<Future<?>> taskList = new ArrayList<>();
            computations.forEach(c -> {
                ComputationExecutionTask task =
                        new ComputationExecutionTask(c,
                                bundleManager.getBundle(c.getDomain()),
                                computationContextService, containerTaskCapacity, computationQueue);
                taskList.add(computationExecutorService.submit(task));
            });

            while(taskList.size() != taskList.stream().filter(Future::isDone).count()) {
                try {
                    Thread.sleep(computationThreadSleepTime);
                } catch (InterruptedException e) {
                    LOGGER.error("createComputationExecutionTask - error of thread sleeping.", e);
                }
            }

            return true;
        } else {
            LOGGER.info("createComputationExecutionTask - Computation is finished!!!");
            return false;
        }
    }

    public void setBundleDeployer(BundleDeployer bundleDeployer) {
        this.bundleDeployer = bundleDeployer;
    }

    public void setPathToClasses(String pathToClasses) {
        this.pathToClasses = pathToClasses;
    }

    public void setBundleDomain(String bundleDomain) {
        this.bundleDomain = bundleDomain;
    }

    public void setBundleManager(BundleManager bundleManager) {
        this.bundleManager = bundleManager;
    }

    public void setComputationContextService(ComputationContextService computationContextService) {
        this.computationContextService = computationContextService;
    }

    public ComputationContextService getComputationContextService() {
        return this.computationContextService;
    }

    public void setActorSystem(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    public void setComputationQueue(ActorRef computationQueue) {
        this.computationQueue = computationQueue;
    }

    public ActorRef getComputationQueue() {
        return this.computationQueue;
    }
}
