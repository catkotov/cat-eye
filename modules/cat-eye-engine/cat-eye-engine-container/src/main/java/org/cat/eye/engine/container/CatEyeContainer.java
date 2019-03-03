package org.cat.eye.engine.container;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.client.ClusterClientReceptionist;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.cat.eye.engine.common.ContainerRole;
import org.cat.eye.engine.common.MsgTopic;
import org.cat.eye.engine.common.deployment.BundleDeployer;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.common.deployment.management.BundleManagerImpl;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.cat.eye.engine.common.service.impl.SimpleComputationContextService;
import org.cat.eye.engine.common.util.CatEyeActorUtil;
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

    private static final String JAR_EXTENSION = ".jar";

    private static String repositoryDirectory = "E:/CatEyeContainer/Repository";
    // only for testing purpose
    private final static ComputationContextService computationContextService = new SimpleComputationContextService();

    private ConcurrentHashMap<String, DomainContext> context = new ConcurrentHashMap<>();

    private BundleManager bundleManager;

    private BundleDeployer bundleDeployer;

    private ContainerRole role;

    public CatEyeContainer(String roleName) {

        ContainerRole containerRole = ContainerRole.defineRole(roleName);

        if (containerRole != null) {

            this.bundleManager = new BundleManagerImpl();
            this.bundleDeployer = new BundleDeployerImpl();
            this.bundleDeployer.setBundleManager(this.bundleManager);

            this.role = containerRole;

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
            domainJars.forEach(this::initBundle);
        }

        LOGGER.info("init - finish initialization of container " + role.name() + ".");
    }

    private void initBundle(String domain, File jar) {

        this.bundleDeployer.deploy(domain, jar.getPath());

        Bundle bundle = this.bundleManager.getBundle(domain);

        ClassLoader bundleClassLoader = bundle.getClassLoader();

        Config domainConfig;

        if (role == ContainerRole.DRIVER) {
            domainConfig = ConfigFactory
                    .parseString("akka.cluster.roles = [" + this.role.getRole() + "]")
                    // TODO get path to config file from properties
                    .withFallback(ConfigFactory.load("test_domain.conf"));
        } else {
            domainConfig = ConfigFactory
                    .parseString("akka.cluster.roles = [" + this.role.getRole() + "]")
                    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 0))
                    // TODO get path to config file from properties
                    .withFallback(ConfigFactory.load("test_domain.conf"));
        }

        ActorSystem domainSystem = ActorSystem.create(domain, domainConfig, bundleClassLoader);

        DomainContext domainContext = new DomainContext(domain, bundle, domainSystem);

        this.context.put(domain, domainContext);

        Cluster.get(domainSystem).registerOnMemberUp(new DomainContextInitializer(role, domainContext));
    }

    private Map<String,File> getDomainJarsFromRepository(String pathToRepository) {

        Map<String,File> result = new HashMap<>();

        File[] domainDirectories = new File(pathToRepository).listFiles();

        if (domainDirectories != null && domainDirectories.length != 0) {
            Arrays.stream(domainDirectories).filter(File::isDirectory).forEach(file -> {
                File[] domainFiles = file.listFiles();
                if (domainFiles != null && domainFiles.length != 0)
                    Arrays.stream(domainFiles)
                            .filter(f -> f.isFile() && f.getName().contains(JAR_EXTENSION))
                            .findFirst()
                            .ifPresent(bundleJar -> result.put(file.getName(), bundleJar));

            });
        }

        return result;
    }

//    public void deploy(String domain, String jarName) {
//
//        String pathToJar = CatEyeContainer.repositoryDirectory + "/" + domain + "/" + jarName;
//
//        File jarFile = new File(pathToJar);
//
//        if (jarFile.exists() && jarFile.isFile()) {
//            initBundle(domain, jarFile);
//        }
//    }

    public static class DomainContextInitializer implements Runnable {

        static final String DRIVER_ACTOR = "-driver-actor";
        static final String DISPATCHER_ACTOR = "-dispatcher-actor";
        static final String ENGINE_ACTOR = "-engine-actor";

        private ContainerRole role;

        private DomainContext domainContext;

        DomainContextInitializer(ContainerRole role, DomainContext domainContext) {
            this.role = role;
            this.domainContext = domainContext;
        }

        public void run() {

            switch (this.role) {
                case DRIVER:
                    driverInit(domainContext.getDomain(), domainContext.getSystem());
                    break;
                case DISPATCHER:
                    dispatcherInit(domainContext.getDomain(), domainContext.getSystem());
                    break;
                case ENGINE:
                    engineInit(domainContext.getDomain(), domainContext.getSystem(), domainContext.getBundle());
                    break;
            }
        }

        private void driverInit(String domain, ActorSystem domainSystem) {
            ActorRef driver = domainSystem.actorOf(Props.create(Driver.class, domain), domain + DRIVER_ACTOR);
            ClusterClientReceptionist.get(domainSystem).registerService(driver);
            ClusterClientReceptionist.get(domainSystem).registerSubscriber(
                    CatEyeActorUtil.getTopicName(domain, MsgTopic.NEW_COMPUTATION), driver);

        }

        private void dispatcherInit(String domain, ActorSystem domainSystem) {
            domainSystem.actorOf(
                    Props.create(
                            Dispatcher.class,
                            domain,
                            CatEyeContainer.computationContextService
                    ),
                    domain + DISPATCHER_ACTOR
            );
        }

        private void engineInit(String domain, ActorSystem domainSystem, Bundle bundle) {
            domainSystem.actorOf(
                    Props.create(
                            Engine.class,
                            domain,
                            bundle,
                            CatEyeContainer.computationContextService
                    ),
                    domain + ENGINE_ACTOR
            );
        }
    }

}
