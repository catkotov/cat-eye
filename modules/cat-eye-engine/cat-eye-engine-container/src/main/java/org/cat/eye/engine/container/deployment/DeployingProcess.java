package org.cat.eye.engine.container.deployment;

import org.cat.eye.common.util.file.ClassFileUtil;
import org.cat.eye.engine.common.deployment.AbstractDeployingProcess;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.common.service.ComputationContextService;
import java.util.List;

public class DeployingProcess extends AbstractDeployingProcess implements Runnable {

    private String jarFilePath;
    private BundleManager bundleManager;

    DeployingProcess(String jarFilePath,
                     String domain,
                     BundleManager bundleManager,
                     ComputationContextService computationContextService) {

        super(domain, computationContextService);

        this.jarFilePath = jarFilePath;
        this.bundleManager = bundleManager;
    }

    @Override
    public void run() {
        List<String> classNameLst = ClassFileUtil.getClassesNamesFromJar(jarFilePath);
        deployBundle(classNameLst, bundleManager);
    }

}
