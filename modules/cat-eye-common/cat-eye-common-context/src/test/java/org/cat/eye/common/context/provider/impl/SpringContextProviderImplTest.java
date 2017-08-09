package org.cat.eye.common.context.provider.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class SpringContextProviderImplTest {
    @Test
    public void start() throws Exception {
        SpringContextProviderImpl.start(TestAppConfig.class.getName()+ ".class");
    }

}