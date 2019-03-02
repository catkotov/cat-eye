package org.cat.eye.engine.container;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.client.ClusterClientReceptionist;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.cat.eye.engine.common.ContainerRole;
import org.cat.eye.engine.common.deployment.BundleDeployer;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.common.deployment.management.BundleManagerImpl;
import org.cat.eye.engine.container.actors.Dispatcher;
import org.cat.eye.engine.container.actors.Driver;
import org.cat.eye.engine.container.actors.Engine;
import org.cat.eye.engine.container.context.DomainContext;
import org.cat.eye.engine.container.deployment.BundleDeployerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kotov on 18.02.2019.
 */
public class CatEyeContainer {

    private final static Logger LOGGER = LoggerFactory.getLogger(CatEyeContainer.class);

    private final static String PATH_TO_JAR =
            "E:/Projects/cat-eye/cat-eye/modules/cat-eye-test-bundle/cat-eye-test-bungle-simple/" +
                    "target/cat-eye-test-bungle-simple-0.1-SNAPSHOT-simple.jar";

    private final static String DOMAIN = "TEST_DOMAIN";

    private static String repositoryDirectory = "E:/CatEyeContainer/Repository";

    private ConcurrentHashMap<String, DomainContext> context = new ConcurrentHashMap<>();

//    private Config config;

    private BundleManager bundleManager;

    private BundleDeployer bundleDeployer;

//    private ActorSystem system;

    private ContainerRole role;

    public CatEyeContainer(String roleName) {

        ContainerRole containerRole = ContainerRole.defineRole(roleName);

        if (containerRole != null) {

            this.bundleManager = new BundleManagerImpl();
            this.bundleDeployer = new BundleDeployerImpl();
            this.bundleDeployer.setBundleManager(this.bundleManager);

            this.role = containerRole;

//            this.config = ConfigFactory
//                    .parseString("akka.cluster.roles = [" + roleName + "]")
//                    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port))
//                    .withFallback(ConfigFactory.load());
//
//
//            this.system = ActorSystem.create("CatEyeContainer", config);
//
//            Cluster.get(system).registerOnMemberUp(this::init);

            init();

        } else {
            LOGGER.error("CatEyeContainer - you must define role for started container!!!");
        }
    }

    private void init() {
        LOGGER.info("init - start initialization of container " + role.name() + ".");
        // check content of bundles repository
        Map<String,File> domainJars = getDomainJarsFromRepository(repositoryDirectory);
        // create context for every domain and init it
        if (!domainJars.isEmpty()) {
            domainJars.forEach((domain, jar) -> {
                initBundle(domain, jar);
            });
        }

        LOGGER.info("init - finish initialization of container " + role.name() + ".");
    }

    private void initBundle(String domain, File jar) {

        this.bundleDeployer.deploy(domain, jar.getPath());

        Bundle bundle = this.bundleManager.getBundle(domain);

        ClassLoader bundleClassLoader = bundle.getClassLoader();

        Config domainConfig; //= ConfigFactory
//                .parseString("akka.cluster.roles = [" + this.role.getRole() + "]")
//                .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port))
//                .withFallback(ConfigFactory.load("test_domain.conf"));

        if (role == ContainerRole.DRIVER) {
            domainConfig = ConfigFactory
                    .parseString("akka.cluster.roles = [" + this.role.getRole() + "]")
                    .withFallback(ConfigFactory.load("test_domain.conf"));
        } else {
            domainConfig = ConfigFactory
                    .parseString("akka.cluster.roles = [" + this.role.getRole() + "]")
                    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 0))
                    .withFallback(ConfigFactory.load("test_domain.conf"));
        }

        ActorSystem domainSystem = ActorSystem.create(domain, domainConfig, bundleClassLoader);

        DomainContext domainContext = new DomainContext(domain, bundle, domainSystem);

        this.context.put(domain, domainContext);

        Cluster.get(domainSystem).registerOnMemberUp(new DomainContextInitializer(domain, domainSystem, role));

//        switch (this.role) {
//            case DRIVER:
//                driverInit(domain, domainSystem);
//                break;
//            case DISPATCHER:
//                dispatcherInit(domain, domainSystem);
//                break;
//            case ENGINE:
//                engineInit(domain, domainSystem);
//                break;
//        }
    }

    private Map<String,File> getDomainJarsFromRepository(String pathToRepository) {

        Map<String,File> result = new HashMap<>();

        File[] domainDirectories = new File(pathToRepository).listFiles();

        if (domainDirectories != null && domainDirectories.length != 0) {
            Arrays.stream(domainDirectories).filter(File::isDirectory).forEach(file -> {
                File[] domainFiles = file.listFiles();
                if (domainFiles != null && domainFiles.length != 0)
                    Arrays.stream(domainFiles)
                            .filter(f -> f.isFile() && f.getName().contains(".jar"))
                            .findFirst()
                            .ifPresent(bundleJar -> result.put(file.getName(), bundleJar));

            });
        }

        return result;
    }

    private void driverInit(String domain, ActorSystem domainSystem) {
        ActorRef driver = domainSystem.actorOf(Props.create(Driver.class, domain), domain + "-driver-actor");
        ClusterClientReceptionist.get(domainSystem).registerService(driver);

    }

    private void dispatcherInit(String domain, ActorSystem domainSystem) {
        domainSystem.actorOf(Props.create(Dispatcher.class, domain), domain + "-dispatcher-actor");
    }

    private void engineInit(String domain, ActorSystem domainSystem) {
        domainSystem.actorOf(Props.create(Engine.class, domain), domain + "-engine-actor");
    }

    public void deploy(String domain, String jarName) {

        String pathToJar = CatEyeContainer.repositoryDirectory + "/" + domain + "/" + jarName;

        File jarFile = new File(pathToJar);

        if (jarFile.exists() && jarFile.isFile()) {
            initBundle(domain, jarFile);
        }
    }

    public static class DomainContextInitializer implements Runnable {

        private String domain;

        private ActorSystem domainSystem;

        private ContainerRole role;

        public DomainContextInitializer(String domain, ActorSystem domainSystem, ContainerRole role) {
            this.domain = domain;
            this.domainSystem = domainSystem;
            this.role = role;
        }

        public void run() {

            switch (this.role) {
                case DRIVER:
                    driverInit(domain, domainSystem);
                    break;
                case DISPATCHER:
                    dispatcherInit(domain, domainSystem);
                    break;
                case ENGINE:
                    engineInit(domain, domainSystem);
                    break;
            }
        }

        private void driverInit(String domain, ActorSystem domainSystem) {
            ActorRef driver = domainSystem.actorOf(Props.create(Driver.class, domain), domain + "-driver-actor");
            ClusterClientReceptionist.get(domainSystem).registerService(driver);

        }

        private void dispatcherInit(String domain, ActorSystem domainSystem) {
            domainSystem.actorOf(Props.create(Dispatcher.class, domain), domain + "-dispatcher-actor");
        }

        private void engineInit(String domain, ActorSystem domainSystem) {
            domainSystem.actorOf(Props.create(Engine.class, domain), domain + "-engine-actor");
        }
    }

}
