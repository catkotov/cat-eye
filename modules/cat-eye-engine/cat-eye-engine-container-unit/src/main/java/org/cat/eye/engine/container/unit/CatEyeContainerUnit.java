package org.cat.eye.engine.container.unit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;
import org.cat.eye.engine.common.deployment.BundleDeployer;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.common.deployment.management.BundleManagerImpl;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.cat.eye.engine.common.service.impl.SimpleComputationContextService;
import org.cat.eye.engine.container.unit.actor.ComputationDriverUnit;
import org.cat.eye.engine.container.unit.deployment.UnitBundleDeployerImpl;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Kotov on 28.10.2018.
 */
public class CatEyeContainerUnit {

    private BundleDeployer bundleDeployer;

    private String pathToClasses;

    private String bundleDomain;

    private ActorSystem actorSystem;

    private BundleManager bundleManager;

    private ComputationContextService computationContextService = new SimpleComputationContextService();

    private CountDownLatch latch = new CountDownLatch(1);

    public CatEyeContainerUnit(String pathToClasses, String bundleDomain) {

        this.pathToClasses = pathToClasses;
        this.bundleDomain = bundleDomain;
        this.bundleDeployer = new UnitBundleDeployerImpl();
        this.bundleManager = new BundleManagerImpl();
        this.bundleDeployer.setBundleManager(bundleManager);
        this.bundleDeployer.setComputationContextService(computationContextService);

        actorSystem = ActorSystem.create("cat-eye-container-unit-actor-system", ConfigFactory.load().getConfig("AkkaContainerUnit"));
    }

    public ActorRef initialize() {
        // deploy bundle
        bundleDeployer.deploy(bundleDomain, pathToClasses);
        Bundle bundle = bundleManager.getBundle(bundleDomain);

        return actorSystem.actorOf(Props.create(ComputationDriverUnit.class, computationContextService, bundle, latch), "driver");
    }

    public CountDownLatch getLatch() {
        return this.latch;
    }
}
