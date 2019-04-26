package org.cat.eye.engine.container.deployment;

import org.cat.eye.common.util.file.ClassFileUtil;
import org.cat.eye.engine.common.deployment.AbstractDeployingProcess;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.common.service.impl.SimpleBundleService;
import org.cat.eye.engine.service.ignite.cache.IgniteBundleService;

import java.util.List;

public class DeployingProcess extends AbstractDeployingProcess implements Runnable {

    private String jarFilePath;
    private BundleManager bundleManager;

    DeployingProcess(String jarFilePath,
                     String domain,
                     BundleManager bundleManager) {

        super(domain);

        this.jarFilePath = jarFilePath;
        this.bundleManager = bundleManager;
    }

    @Override
    public void run() {
        super.setBundleService(new IgniteBundleService(new String[] {"127.0.0.1:10800"}, 10 , 10, 10));
        List<String> classNameLst = ClassFileUtil.getClassesNamesFromJar(jarFilePath);
        deployBundle(classNameLst, bundleManager);
    }

}
