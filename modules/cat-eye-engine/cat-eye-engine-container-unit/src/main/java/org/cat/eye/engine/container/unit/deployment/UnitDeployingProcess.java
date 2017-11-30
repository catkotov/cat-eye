package org.cat.eye.engine.container.unit.deployment;

import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Kotov on 30.11.2017.
 */
public class UnitDeployingProcess implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(UnitDeployingProcess.class);

    private String classPath;
    private String domain;
    private BundleManager bundleManager;

    public UnitDeployingProcess(String classPath, String domain, BundleManager bundleManager) {
        this.classPath = classPath;
        this.domain = domain;
        this.bundleManager = bundleManager;
    }

    @Override
    public void run() {


    }
}
