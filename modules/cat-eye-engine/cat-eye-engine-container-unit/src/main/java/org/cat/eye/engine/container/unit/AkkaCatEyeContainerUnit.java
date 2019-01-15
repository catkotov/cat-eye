package org.cat.eye.engine.container.unit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.cat.eye.engine.common.deployment.BundleDeployer;
import org.cat.eye.engine.common.deployment.management.BundleManagerImpl;
import org.cat.eye.engine.container.unit.deployment.UnitBundleDeployerImpl;

/**
 * Created by Kotov on 28.10.2018.
 */
public class AkkaCatEyeContainerUnit {

    private BundleDeployer bundleDeployer;

    private String pathToClasses;

    private String bundleDomain;

    private ActorSystem actorSystem;
    private ActorRef supervisor;

    public AkkaCatEyeContainerUnit(String pathToClasses, String bundleDomain) {

        this.pathToClasses = pathToClasses;
        this.bundleDomain = bundleDomain;
        this.bundleDeployer = new UnitBundleDeployerImpl();
        this.bundleDeployer.setBundleManager(new BundleManagerImpl());

        actorSystem = ActorSystem.create("cat-eye-container-unit-actor-system");
        supervisor = actorSystem.actorOf(Props.create(CatEyeContainerSuperviser.class));
    }

    public void initialize() {
        // deploy bundle
        bundleDeployer.deploy(pathToClasses, bundleDomain);

    }
}
