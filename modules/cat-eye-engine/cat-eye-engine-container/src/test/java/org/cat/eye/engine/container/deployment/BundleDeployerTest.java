package org.cat.eye.engine.container.deployment;

import org.junit.Test;

import static org.junit.Assert.*;

public class BundleDeployerTest {

    private final static String PATH_TO_JAR =
            "E:/Projects/cat-eye/cat-eye/modules/cat-eye-test-bundle/cat-eye-test-bungle-simple/" +
                    "target/cat-eye-test-bungle-simple-0.1-SNAPSHOT-simple.jar";

    @Test
    public void deploy() throws Exception {

        BundleDeployer deployer = new BundleDeployer();

        deployer.deploy(PATH_TO_JAR);
    }

}