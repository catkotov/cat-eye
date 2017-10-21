package org.cat.eye.engine.container.deployment;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class DeployingProcess implements Runnable {

    private String jarFilePath;

    public DeployingProcess(String jarFilePath) {
        this.jarFilePath = jarFilePath;
    }

    @Override
    public void run() {
        // search of annotated classes


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
