package org.cat.eye.engine.container.deployment;

import org.cat.eye.engine.container.deployment.management.BundleManager;
import org.cat.eye.engine.container.deployment.management.BundleManagerImpl;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class BundleDeployerTest {

    private final static String PATH_TO_JAR =
            "E:/Projects/cat-eye/cat-eye/modules/cat-eye-test-bundle/cat-eye-test-bungle-simple/" +
                    "target/cat-eye-test-bungle-simple-0.1-SNAPSHOT-simple.jar";

    private final static String DOMAIN = "TEST_DOMAIN";

    @Ignore
    @Test
    public void deploy() throws Exception {
        BundleManager bundleManager = new BundleManagerImpl();

        BundleDeployer deployer = new BundleDeployer();
        deployer.setBundleManager(bundleManager);

        deployer.deploy(PATH_TO_JAR, DOMAIN);
    }

}