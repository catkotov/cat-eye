package org.cat.eye.engine.container.deployment;

import org.cat.eye.common.util.file.ClassFileUtil;
import org.cat.eye.engine.common.deployment.AbstractDeployingProcess;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class DeployingProcess extends AbstractDeployingProcess implements Runnable {

    private String jarFilePath;
    private String domain;
    private BundleManager bundleManager;

    DeployingProcess(String jarFilePath, String domain, BundleManager bundleManager) {
        this.jarFilePath = jarFilePath;
        this.domain = domain;
        this.bundleManager = bundleManager;
    }

    @Override
    public void run() {
        List<String> classNameLst = ClassFileUtil.getClassesNamesFromJar(jarFilePath);
        deployBundle(classNameLst, domain, bundleManager);
    }

}
