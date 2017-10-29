package org.cat.eye.engine.container.impl;

import org.cat.eye.common.context.provider.impl.SpringContextProviderImpl;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class CatEyeContainerImplITManual {
    @Ignore
    @Test
    public void startContainer() throws Exception {
        SpringContextProviderImpl.start(TestCatEyeContainerConfiguration.class.getName() + ".class");
    }

}