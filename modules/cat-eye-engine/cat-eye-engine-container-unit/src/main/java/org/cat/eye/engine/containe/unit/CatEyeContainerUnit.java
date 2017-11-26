package org.cat.eye.engine.containe.unit;

import org.cat.eye.engine.common.CatEyeContainer;
import org.cat.eye.engine.common.deployment.BundleDeployer;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Kotov on 24.11.2017.
 */
public class CatEyeContainerUnit implements CatEyeContainer {

    // container name
    private String name;

    private BundleDeployer bundleDeployer;

    private String pathToBundleJar;

    private String bundleDomain;

    private final static int DEFAULT_COMPUTATION_THREAD_POOL_SIZE = 4;
    private AtomicInteger computationThreadPoolSize = new AtomicInteger(DEFAULT_COMPUTATION_THREAD_POOL_SIZE);

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

    @PostConstruct
    private void initialize() throws Exception {
        // deploy bundle
        bundleDeployer.deploy(pathToBundleJar, bundleDomain);
        // start computation work flow
        initComputationProcess();
    }

    private void initComputationProcess() {
//        isRunning.set(true);
//        initComputationThreadPool();
//
//        initComputeThread();

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
