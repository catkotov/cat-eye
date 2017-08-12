package org.cat.eye.engine.container.impl;

import org.cat.eye.engine.container.CatEyeContainer;
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
        return new CatEyeContainerImpl();
    }
}
