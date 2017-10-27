package org.cat.eye.engine.container.deployment;

import org.cat.eye.common.util.file.JarFileUtil;
import org.cat.eye.engine.container.model.MethodSpecification;
import org.cat.eye.engine.model.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

public class DeployingProcess implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(DeployingProcess.class);

    private String jarFilePath;

    public DeployingProcess(String jarFilePath) {
        this.jarFilePath = jarFilePath;
    }

    @Override
    public void run() {
        // search of annotated classes
        List<String> classNameLst = JarFileUtil.getClassesNames(jarFilePath);
        for (String className : classNameLst) {
            try {
                Class<?> bundleClass = Thread.currentThread().getContextClassLoader().loadClass(className);

                Map<Class<?>, Set<MethodSpecification>> computables = new ConcurrentHashMap<>();

                if (bundleClass.isAnnotationPresent(IsComputable.class)) {
                    LOGGER.info("DeployingProcess.run - class <" + className + "> is computable!");

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

                    computables.put(bundleClass, methodSpecificationSet);
                }
            } catch (ClassNotFoundException e) {
                LOGGER.error("DeployingProcess.run - can't process class <" + className + ">.", e);
            }
        }

        // create and store bundle context

    }

    private List<String> getClassesNames(String jarFilePath) {

        File file = new File(jarFilePath);

        if (file.exists() && file.getName().endsWith(".jar")) {
            try {
                JarFile jarFile = new JarFile(file);
                jarFile.entries();

            } catch (IOException e) {

            }
        } else {

        }

        return null;
    }
}
