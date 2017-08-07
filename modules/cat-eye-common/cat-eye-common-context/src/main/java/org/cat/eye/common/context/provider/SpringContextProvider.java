package org.cat.eye.common.context.provider;

import org.cat.eye.common.context.provider.exception.CloseAppContextException;
import org.springframework.context.ApplicationContext;

public interface SpringContextProvider {

    ApplicationContext getApplicationContext();

    ClassLoader getClassLoader();

    void close() throws CloseAppContextException;
}
