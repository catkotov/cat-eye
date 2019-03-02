package org.cat.eye.engine.common.deployment;

import org.cat.eye.engine.common.crusher.computation.ComputationFactory;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.common.deployment.management.BundleManagerImpl;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.model.MethodSpecification;
import org.cat.eye.engine.container.deployment.BundleDeployerImpl;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BundleDeployerTest {

    private final static String PATH_TO_JAR =
            "E:/Projects/cat-eye/cat-eye/modules/cat-eye-test-bundle/cat-eye-test-bungle-simple/" +
                    "target/cat-eye-test-bungle-simple-0.1-SNAPSHOT-simple.jar";

    private final static String DOMAIN = "TEST_DOMAIN";

    @Ignore
    @Test
    public void deploy() throws Exception {
        BundleManager bundleManager = new BundleManagerImpl();

        BundleDeployer deployer = new BundleDeployerImpl();
        deployer.setBundleManager(bundleManager);

        deployer.deploy(DOMAIN, PATH_TO_JAR);

        Bundle bundle = bundleManager.getBundle(DOMAIN);

        ClassLoader bundleCL = bundle.getClassLoader();

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(bundleCL);

        Map<Class<?>, Set<MethodSpecification>> computables = bundle.getComputables();

        for (Map.Entry<Class<?>, Set<MethodSpecification>> entry : computables.entrySet()) {
            Class<?> clazz = entry.getKey();
            Object computer = clazz.newInstance();
            Computation computation = ComputationFactory.create(computer, null, DOMAIN);

            String computerName = computation.getComputerName();
            String domain = computation.getDomain();
            UUID id = computation.getId();
            Object comp = computation.getComputer();

            Set<MethodSpecification> methods = computables.get(comp.getClass());
            MethodSpecification method = methods.stream().filter(spec -> spec.getStep() == computation.getNextStep()).findAny().get();

            List<?> result = (List<?>) method.getMethod().invoke(comp);

            computation.setNextStep(computation.getNextStep() + 1);

            result.size();

            method = methods.stream().filter(spec -> spec.getStep() == computation.getNextStep()).findAny().get();

            Parameter[] parameters = method.getParameters();

            int i = parameters.length;
        }

        Thread.currentThread().setContextClassLoader(cl);
    }

}