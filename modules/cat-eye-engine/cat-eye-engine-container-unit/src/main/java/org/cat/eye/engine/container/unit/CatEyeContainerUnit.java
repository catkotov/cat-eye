package org.cat.eye.engine.container.unit;

import org.cat.eye.engine.common.CatEyeContainer;
import org.cat.eye.engine.common.CatEyeContainerTaskCapacity;
import org.cat.eye.engine.common.crusher.ComputationExecutionTask;
import org.cat.eye.engine.common.deployment.BundleDeployer;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Kotov on 24.11.2017.
 */
public class CatEyeContainerUnit implements CatEyeContainer {

    public static final Logger LOGGER = LoggerFactory.getLogger(CatEyeContainerUnit.class);

    // container name
    private String name;

    private BundleDeployer bundleDeployer;

    private BundleManager bundleManager;

    private String pathToClasses;

    private String bundleDomain;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private final static int DEFAULT_COMPUTATION_THREAD_POOL_SIZE = 4;
    private AtomicInteger computationThreadPoolSize = new AtomicInteger(DEFAULT_COMPUTATION_THREAD_POOL_SIZE);
    private ExecutorService computationExecutorService;
    private final static String COMPUTATION_THREAD_NAME_PREFIX = "COMPUTATION-THREAD-";

    private final CatEyeContainerTaskCapacity containerTaskCapacity =
            new CatEyeContainerTaskCapacity(this.computationThreadPoolSize.get());

    private Thread computeThread;

    private long computationThreadSleepTime = 100L;

    private ComputationContextService computationContextService;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
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
            computeThread.start();
            LOGGER.info("CatEyeContainerUnit.initComputeThread - compute thread was started.");
        } else {
            LOGGER.info("CatEyeContainerUnit.initComputeThread - compute thread is already running.");
        }
    }

    private void computeLoop() {
        LOGGER.info("CatEyeContainerUnit.computeLoop - start the compute loop.");
        while (isRunning.get()) {
            containerTaskCapacity.await();
            // create and submit task
            createComputationExecutionTask();
            // sleep if container task capacity is exhausted
            if (containerTaskCapacity.getRemaining() <= 0) {
                try {
                    Thread.sleep(computationThreadSleepTime);
                } catch (InterruptedException e) {
                    LOGGER.error("CatEyeContainerUnit.computeLoop - exception during computation thread sleeping.", e);
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
}
