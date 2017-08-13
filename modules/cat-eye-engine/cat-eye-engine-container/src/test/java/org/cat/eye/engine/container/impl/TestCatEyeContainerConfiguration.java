package org.cat.eye.engine.container.impl;

import org.cat.eye.engine.container.CatEyeContainer;
import org.cat.eye.engine.container.discovery.DatagramReceiver;
import org.cat.eye.engine.container.discovery.DatagramSender;
import org.cat.eye.engine.container.discovery.NeighboursDiscoveryLeadingLight;
import org.cat.eye.engine.container.discovery.NeighboursDiscoveryReceiver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * Created by Kotov on 12.08.2017.
 */
@Configuration
public class TestCatEyeContainerConfiguration {

    @Bean
    CatEyeContainer getCatEyeContainer() {
        CatEyeContainerImpl container = new CatEyeContainerImpl();
        container.setLeadingLight(getLeaderLight());
        container.setDatagramSender(getDatagramSender());
        container.setDatagramReceiver(getDatagramReceiver());
        container.setNeighbourDiscoveryReceiver(getNeighbourDiscoveryReceiver());
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
}
