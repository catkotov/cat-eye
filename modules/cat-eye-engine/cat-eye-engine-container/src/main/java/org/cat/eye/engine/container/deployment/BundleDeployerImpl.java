package org.cat.eye.engine.container.deployment;

import org.cat.eye.engine.common.deployment.BundleClassLoader;
import org.cat.eye.engine.common.deployment.BundleDeployer;
import org.cat.eye.engine.common.deployment.management.BundleManager;
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
public class BundleDeployerImpl implements BundleDeployer {

    private BundleManager bundleManager;

    private final static Logger LOGGER = LoggerFactory.getLogger(BundleDeployerImpl.class);

    public void deploy(String pathToJar, String domain) {
        // get urls of jar's file
        URL[] ursl = getUrlsFromJarFile(pathToJar);
        // create class loader for bundle
        URLBundleClassLoader bundleClassLoader = new URLBundleClassLoader(ursl, new BundleClassLoader());
        // create and start thread for bundle deploying
        Thread deployingThread = new Thread(new DeployingProcess(pathToJar, domain, bundleManager));
        deployingThread.setContextClassLoader(bundleClassLoader);
        deployingThread.start();
        try {
            deployingThread.join();
        } catch (InterruptedException e) {
            LOGGER.error("BundleDeployer.deploy - can't deploy bundle: " + pathToJar, e);
        }
    }

    private URL[] getUrlsFromJarFile(String pathToJar) {

        URL[] result = null;

        File file = new File(pathToJar);

        if (file.exists() && file.getName().endsWith(".jar")) {
            try {
                List<URL> urls = new ArrayList<>();
                urls.add(file.toURI().toURL());

                JarFile jarFile = new JarFile(file);
                Manifest manifest = jarFile.getManifest();

                if (manifest != null) {
                    String classPath = manifest.getMainAttributes().getValue("class-path");
                    if (classPath != null) {
                        for (String cp : classPath.split("\\s+")) {
                            File lib = new File(file.getParent(), cp);
                            urls.add(lib.toURI().toURL());
                        }
                        result = urls.toArray(new URL[urls.size()]);
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

    public void setBundleManager(BundleManager bundleManager) {
        this.bundleManager = bundleManager;
    }
}
