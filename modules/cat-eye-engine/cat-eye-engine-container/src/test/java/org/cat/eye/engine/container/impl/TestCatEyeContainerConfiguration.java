package org.cat.eye.engine.container.impl;

import org.cat.eye.engine.common.CatEyeContainer;
import org.cat.eye.engine.common.deployment.BundleDeployer;
import org.cat.eye.engine.container.datagram.DatagramReceiver;
import org.cat.eye.engine.container.datagram.DatagramSender;
import org.cat.eye.engine.container.deployment.BundleDeployerImpl;
import org.cat.eye.engine.common.deployment.management.BundleManager;
import org.cat.eye.engine.common.deployment.management.BundleManagerImpl;
import org.cat.eye.engine.container.discovery.NeighboursDiscoveryLeadingLight;
import org.cat.eye.engine.container.discovery.NeighboursDiscoveryReceiver;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.cat.eye.engine.container.service.impl.SimpleComputationContextService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * Created by Kotov on 12.08.2017.
 */
@Configuration
public class TestCatEyeContainerConfiguration {

    private final static String PATH_TO_JAR =
            "E:/Projects/cat-eye/cat-eye/modules/cat-eye-test-bundle/cat-eye-test-bungle-simple/" +
                    "target/cat-eye-test-bungle-simple-0.1-SNAPSHOT-simple.jar";

    private final static String DOMAIN = "TEST_DOMAIN";

    @Bean
    CatEyeContainer getCatEyeContainer() {
        CatEyeContainerImpl container = new CatEyeContainerImpl();
        container.setName(System.getProperty("cat.eye.container.name"));
        container.setLeadingLight(getLeaderLight());
        container.setDatagramSender(getDatagramSender());
        container.setDatagramReceiver(getDatagramReceiver());
        container.setNeighbourDiscoveryReceiver(getNeighbourDiscoveryReceiver());
        container.setBundleManager(getBundleManager());
        container.setBundleDeployer(getBundleDeployer());
        container.setPathToBundleJar(PATH_TO_JAR);
        container.setBundleDomain(DOMAIN);
        container.setComputationContextService(getComputationContextService());
        return container;
    }

    @Bean
    DatagramSender getDatagramSender() {
        return new DatagramSender(256);
    }

    @Bean
    NeighboursDiscoveryLeadingLight getLeaderLight() {
        NeighboursDiscoveryLeadingLight leadingLight = new NeighboursDiscoveryLeadingLight();
        leadingLight.setDatagramSender(getDatagramSender());
        return leadingLight;
    }

    @Bean
    DatagramReceiver getDatagramReceiver() {
        return new DatagramReceiver(256);
    }

    @Bean
    NeighboursDiscoveryReceiver getNeighbourDiscoveryReceiver() {
        NeighboursDiscoveryReceiver receiver = new NeighboursDiscoveryReceiver();
        receiver.setDatagramReceiver(getDatagramReceiver());
        return receiver;
    }

    @Bean
    BundleManager getBundleManager() {
        return new BundleManagerImpl();
    }

    @Bean
    BundleDeployer getBundleDeployer() {
        BundleDeployer bundleDeployer = new BundleDeployerImpl();
        bundleDeployer.setBundleManager(getBundleManager());
        return bundleDeployer;
    }

    @Bean
    ComputationContextService getComputationContextService() {
        return new SimpleComputationContextService();
    }
}
