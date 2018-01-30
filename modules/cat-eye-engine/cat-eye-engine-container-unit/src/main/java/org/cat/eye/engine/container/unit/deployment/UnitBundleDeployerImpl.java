package org.cat.eye.engine.container.unit.deployment;

import org.cat.eye.engine.common.deployment.BundleClassLoader;
import org.cat.eye.engine.common.deployment.BundleDeployer;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Kotov on 30.11.2017.
 */
public class UnitBundleDeployerImpl implements BundleDeployer {

    private final static Logger LOGGER = LoggerFactory.getLogger(UnitBundleDeployerImpl.class);

    private BundleManager bundleManager;

    @Override
    public void deploy(String classPath, String domain) {

        ClassLoader bundleClassLoader = Thread.currentThread().getContextClassLoader(); // new BundleClassLoader();

        // create and start thread for bundle deploying
        Thread deployingThread = new Thread(new UnitDeployingProcess(classPath, domain, bundleManager));
        deployingThread.setContextClassLoader(bundleClassLoader);
        deployingThread.start();
        try {
            deployingThread.join();
        } catch (InterruptedException e) {
            LOGGER.error("deploy - can't deploy bundle: " + classPath, e);
        }
    }

    @Override
    public void setBundleManager(BundleManager bundleManager) {
        this.bundleManager = bundleManager;
    }
}
