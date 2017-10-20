package org.cat.eye.engine.container.deployment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Created by Kotov on 20.10.2017.
 */
public class BundleDeployer {

    private final static Logger LOGGER = LoggerFactory.getLogger(BundleDeployer.class);

    public void deploy(String pathToJar) {

        URL[] ursl = getUrlsFromJarFile(pathToJar);




    }

    private URL[] getUrlsFromJarFile(String pathToJar) {

        URL[] result = null;

        File file = new File(pathToJar);

        if (file.exists() && file.getName().endsWith(".jar")) {
            try {
                JarFile jarFile = new JarFile(file);
                Manifest manifest = jarFile.getManifest();
                if (manifest != null) {
                    String classPath = manifest.getMainAttributes().getValue("class-path");
                    if (classPath != null) {
                        List<URL> urls = new ArrayList<>();
                        for (String cp : classPath.split("\\s+")) {
                            File lib = new File(file.getParent(), cp);
                            urls.add(lib.toURI().toURL());
                        }

                        result = (URL[]) urls.toArray();
                    }
                }
            } catch (IOException e) {
                LOGGER.error("BundleDeployer.deploy - ", e);
            }

        } else {
            LOGGER.warn("BundleDeployer.deploy - ");
        }

        return result;
    }
}
