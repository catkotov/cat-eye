package org.cat.eye.engine.common.deployment;

import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.common.service.ComputationContextService;

public interface BundleDeployer {

    Bundle deploy(String domain, String path);

    void setBundleManager(BundleManager bundleManager);
}
