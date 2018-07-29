package org.cat.eye.engine.container.unit;

import akka.actor.ActorSystem;
import org.cat.eye.common.context.akka.SpringExtention;
import org.cat.eye.engine.common.deployment.BundleDeployer;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.common.deployment.management.BundleManagerImpl;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.cat.eye.engine.common.service.impl.SimpleComputationContextService;
import org.cat.eye.engine.container.unit.deployment.UnitBundleDeployerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Kotov on 29.12.2017.
 */
@Configuration
@ComponentScan(basePackages = {"org.cat.eye.engine.common.service"})
public class CatEyeContainerUnitConfig {

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    CatEyeContainerUnit getCatEyeContainerUnit() {
        CatEyeContainerUnit container = new CatEyeContainerUnit();
        container.setName("UNIT_TEST_CONTAINER");
        container.setBundleManager(getBundleManager());
        container.setBundleDeployer(getBundleDeployer());
        container.setComputationContextService(getComputationContextService());
        container.setActorSystem(getActorSystem());

        return container;
    }

    @Bean
    BundleManager getBundleManager() {
        return new BundleManagerImpl();
    }

    @Bean
    BundleDeployer getBundleDeployer() {
        BundleDeployer bundleDeployer = new UnitBundleDeployerImpl();
        bundleDeployer.setBundleManager(getBundleManager());
        return bundleDeployer;
    }

    @Bean
    ComputationContextService getComputationContextService() {
        return new SimpleComputationContextService();
    }

    @Bean
    ActorSystem getActorSystem() {
        ActorSystem system = ActorSystem.create("cat-eye-container-actor-system");
        SpringExtention.SPRING_EXTENTION_PROVIDER.get(system).initialize(applicationContext);
        return system;
    }

}
