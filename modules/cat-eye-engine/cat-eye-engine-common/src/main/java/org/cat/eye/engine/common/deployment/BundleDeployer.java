package org.cat.eye.engine.common.deployment;

import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.deployment.management.BundleManager;

public interface BundleDeployer {

    Bundle deploy(String domain, String path);

    void setBundleManager(BundleManager bundleManager);
}
