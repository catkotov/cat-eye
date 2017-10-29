package org.cat.eye.engine.container.deployment;

import org.cat.eye.common.util.file.JarFileUtil;
import org.cat.eye.engine.container.deployment.management.Bundle;
import org.cat.eye.engine.container.deployment.management.BundleImpl;
import org.cat.eye.engine.container.deployment.management.BundleManager;
import org.cat.eye.engine.container.model.MethodSpecification;
import org.cat.eye.engine.model.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class DeployingProcess implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(DeployingProcess.class);

    private String jarFilePath;
    private String domain;
    private BundleManager bundleManager;

    DeployingProcess(String jarFilePath, String domain, BundleManager bundleManager) {
        this.jarFilePath = jarFilePath;
        this.domain = domain;
        this.bundleManager = bundleManager;
    }

    @Override
    public void run() {

        List<String> classNameLst = JarFileUtil.getClassesNames(jarFilePath);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Map<Class<?>, Set<MethodSpecification>> computables = new ConcurrentHashMap<>();
        // search of annotated classes
        for (String className : classNameLst) {
            try {
                Class<?> bundleClass = classLoader.loadClass(className);

                if (!bundleClass.isAnnotationPresent(IsComputable.class)) {
                    continue;
                }

                LOGGER.info("DeployingProcess.run - class <" + className + "> is computable!");

                Set<MethodSpecification> methodSpecificationSet = getMethodSpecifications(bundleClass);

                computables.put(bundleClass, methodSpecificationSet);

            } catch (ClassNotFoundException e) {
                LOGGER.error("DeployingProcess.run - can't process class <" + className + ">.", e);
            }
        }
        // create and store bundle context
        if (!computables.isEmpty()) {
            Bundle bundle = new BundleImpl(domain, computables, classLoader);
            bundleManager.putBundle(bundle);
            LOGGER.info("DeployingProcess.run - bundle " + bundle.getDomain() + " was deployed.");
        }
    }

    private Set<MethodSpecification> getMethodSpecifications(Class<?> bundleClass) {

        Set<MethodSpecification> methodSpecificationSet = null;

        Method[] methods = bundleClass.getMethods();
        if (methods != null && methods.length != 0) {

            methodSpecificationSet = new TreeSet<>();

            for (Method method : methods) {
                if (method.isAnnotationPresent(Compute.class)) {
                    int step = method.getAnnotation(Compute.class).step();
                    Map<Parameter, Annotation> parameterAnnotationMap = null;
                    Parameter[] parameters = method.getParameters();
                    if (parameters != null && parameters.length != 0) {
                        parameterAnnotationMap = new ConcurrentHashMap<>();
                        for (Parameter parameter : parameters) {
                            if (parameter.isAnnotationPresent(In.class)) {
                                parameterAnnotationMap.put(parameter, parameter.getAnnotation(In.class));
                            } else if (parameter.isAnnotationPresent(Out.class)) {
                                parameterAnnotationMap.put(parameter, parameter.getAnnotation(Out.class));
                            } else if (parameter.isAnnotationPresent(InOut.class)) {
                                parameterAnnotationMap.put(parameter, parameter.getAnnotation(InOut.class));
                            }
                        }
                    }

                    methodSpecificationSet.add(new MethodSpecification(method, step, parameterAnnotationMap));
                }
            }
        }
        return methodSpecificationSet;
    }

}
