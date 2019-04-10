package org.cat.eye.engine.container.unit.deployment;

import org.cat.eye.common.util.file.ClassFileUtil;
import org.cat.eye.engine.common.deployment.AbstractDeployingProcess;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.common.service.ComputationContextService;
import java.util.List;

/**
 * Created by Kotov on 30.11.2017.
 */
public class UnitDeployingProcess extends AbstractDeployingProcess implements Runnable {

    private String classPath;
    private BundleManager bundleManager;

    UnitDeployingProcess(String classPath,
                         String domain,
                         BundleManager bundleManager,
                         ComputationContextService computationContextService) {

        super(domain, computationContextService);

        this.classPath = classPath;
        this.bundleManager = bundleManager;
    }

    @Override
    public void run() {
        List<String> classNameLst = ClassFileUtil.getClassNamesFromPath(classPath);
        deployBundle(classNameLst, bundleManager);
    }
}
