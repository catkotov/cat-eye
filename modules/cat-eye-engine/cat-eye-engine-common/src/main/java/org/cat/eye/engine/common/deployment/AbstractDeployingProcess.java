package org.cat.eye.engine.common.deployment;

import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.deployment.management.BundleImpl;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.common.model.MethodSpecification;
import org.cat.eye.engine.common.service.BundleService;
import org.cat.eye.engine.model.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kotov on 10.12.2017.
 */
public class AbstractDeployingProcess {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractDeployingProcess.class);

    private String domain;
    private BundleService bundleService;

    public AbstractDeployingProcess(String domain) {
        this.domain = domain;
    }

    protected void deployBundle(List<String> classNameLst, BundleManager bundleManager) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Map<Class<?>, Set<MethodSpecification>> computables = new ConcurrentHashMap<>();
        // search of annotated classes
        for (String className : classNameLst) {
            try {
                Class<?> bundleClass = classLoader.loadClass(className);

                if (!bundleClass.isAnnotationPresent(IsComputable.class)) {
                    continue;
                }

                LOGGER.info("deployBundle - class <" + className + "> is computable!");

                Set<MethodSpecification> methodSpecificationSet = getMethodSpecifications(bundleClass);

                computables.put(bundleClass, methodSpecificationSet);

            } catch (Exception e) {
                LOGGER.error("deployBundle - can't process class <" + className + ">.", e);
            }
        }
        // create and store bundle context
        if (!computables.isEmpty()) {
            Bundle bundle = new BundleImpl(domain, computables, classLoader, bundleService);
            bundleManager.putBundle(bundle);
            LOGGER.info("deployBundle - bundle " + bundle.getDomain() + " was deployed.");
        }
    }

    private Set<MethodSpecification> getMethodSpecifications(Class<?> bundleClass) {

        Set<MethodSpecification> methodSpecificationSet = null;

        Method[] methods = bundleClass.getMethods();
        // TODO simplify
        if (methods.length != 0) {

            methodSpecificationSet = new TreeSet<>();

            for (Method method : methods) {
                if (method.isAnnotationPresent(Compute.class)) {
                    int step = method.getAnnotation(Compute.class).step();
                    Parameter[] parameters = method.getParameters();
                    if (parameters != null && parameters.length != 0) {

                        for (Parameter parameter : parameters) {
                            if (parameter.isAnnotationPresent(In.class)
                                    || parameter.isAnnotationPresent(Out.class)
                                    || parameter.isAnnotationPresent(InOut.class)) {

                                try {
                                    if (bundleService.getArgument(parameter, domain) == null) {

                                        bundleService.setArgument(
                                                parameter,
                                                domain,
                                                parameter.getType().newInstance()
                                        );

                                        LOGGER.info("getMethodSpecifications - parameter ["
                                                + parameter.getType().getSimpleName() + "] was instantiated.");
                                    }
                                } catch (Exception e) {
                                    // TODO maybe we need to throw exception to upper catcher
                                    LOGGER.error("getMethodSpecifications - can't create argument for parameter "
                                            + parameter.getName(), e);
                                }
                            } else {
                                String errorMsg =
                                        String.format("Parameter [%s] of method [%s] in class [%s] is not annotated!!!",
                                        parameter.getName(), method.getName(), bundleClass.getName());
                                throw new RuntimeException(errorMsg);
                            }
                        }
                    }

                    methodSpecificationSet.add(new MethodSpecification(method, step, parameters));
                }
            }
        }
        return methodSpecificationSet;
    }

    protected void setBundleService(BundleService bundleService) {
        this.bundleService = bundleService;
    }
}
