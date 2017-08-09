package org.cat.eye.common.context.provider.impl;

import org.cat.eye.common.context.provider.msg.MessageServiceMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 *
 * Created by Kotov on 28.08.2016.
 */
@Configuration
@ComponentScan
public class TestAppConfig {

    @Bean
    MessageServiceMock getMessageService() {
        return new MessageServiceMock() {
            @Override
            public String getMessage() {
                return "Hello from Spring!!!";
            }
        };
    }
}
