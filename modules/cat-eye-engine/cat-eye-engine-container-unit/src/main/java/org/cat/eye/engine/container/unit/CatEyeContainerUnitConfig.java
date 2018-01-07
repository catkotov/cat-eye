package org.cat.eye.engine.container.unit;

import org.cat.eye.engine.common.deployment.BundleDeployer;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.common.deployment.management.BundleManagerImpl;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.cat.eye.engine.common.service.impl.SimpleComputationContextService;
import org.cat.eye.engine.container.unit.deployment.UnitBundleDeployerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Kotov on 29.12.2017.
 */
@Configuration
public class CatEyeContainerUnitConfig {

    @Bean
    CatEyeContainerUnit getCatEyeContainerUnit() {
        CatEyeContainerUnit container = new CatEyeContainerUnit();
        container.setName("UNIT_TEST_CONTAINER");
        container.setBundleManager(getBundleManager());
        container.setBundleDeployer(getBundleDeployer());

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
}
