package org.cat.eye.engine.common.deployment;

import org.cat.eye.engine.common.deployment.management.BundleManager;

public interface BundleDeployer {

    void deploy(String path, String domain);

    void setBundleManager(BundleManager bundleManager);
}
