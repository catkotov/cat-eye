package org.cat.eye.common.context.provider.impl;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore // This is test for manual start
public class SpringContextProviderImplITManual {

    @Test
    public void start() throws Exception {
        SpringContextProviderImpl.start(TestAppConfig.class.getName()+ ".class");
    }

}