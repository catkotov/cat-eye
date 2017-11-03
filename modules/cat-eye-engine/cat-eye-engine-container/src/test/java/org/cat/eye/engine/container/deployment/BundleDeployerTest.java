package org.cat.eye.engine.container.deployment;

import org.cat.eye.engine.container.crusher.computetion.ComputationFactory;
import org.cat.eye.engine.container.deployment.management.Bundle;
import org.cat.eye.engine.container.deployment.management.BundleManager;
import org.cat.eye.engine.container.deployment.management.BundleManagerImpl;
import org.cat.eye.engine.container.model.Computation;
import org.cat.eye.engine.container.model.MethodSpecification;
import org.junit.Ignore;
import org.junit.Test;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

public class BundleDeployerTest {

    private final static String PATH_TO_JAR =
            "D:/Sand-box/cat-eye/modules/cat-eye-test-bundle/cat-eye-test-bungle-simple/" +
                    "target/cat-eye-test-bungle-simple-0.1-SNAPSHOT-simple.jar";

    private final static String DOMAIN = "TEST_DOMAIN";

    @Ignore
    @Test
    public void deploy() throws Exception {
        BundleManager bundleManager = new BundleManagerImpl();

        BundleDeployer deployer = new BundleDeployer();
        deployer.setBundleManager(bundleManager);

        deployer.deploy(PATH_TO_JAR, DOMAIN);

        Bundle bundle = bundleManager.getBundle(DOMAIN);

        ClassLoader bundleCL = bundle.getClassLoader();

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(bundleCL);

        Map<Class<?>, Set<MethodSpecification>> computables = bundle.getComputables();

        for (Map.Entry<Class<?>, Set<MethodSpecification>> entry : computables.entrySet()) {
            Class<?> clazz = entry.getKey();
            Object computer = clazz.newInstance();
            Computation computation = ComputationFactory.create(computer, DOMAIN);

            String computerName = computation.getComputerName();
            String domain = computation.getDomain();
            UUID id = computation.getId();
            Object comp = computation.getComputer();
        }

        Thread.currentThread().setContextClassLoader(cl);
    }

}