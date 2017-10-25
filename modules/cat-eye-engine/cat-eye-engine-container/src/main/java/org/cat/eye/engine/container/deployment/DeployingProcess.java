package org.cat.eye.engine.container.deployment;

import org.cat.eye.common.util.file.JarFileUtil;
import org.cat.eye.engine.model.annotation.IsComputable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;
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
                if (bundleClass.isAnnotationPresent(IsComputable.class)) {
                    LOGGER.info("DeployingProcess.run - class <" + className + "> is computable!");
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
