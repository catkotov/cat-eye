package org.cat.eye.common.context.provider.impl;

import org.cat.eye.common.context.provider.AnnotationAppContextCreator;
import org.cat.eye.common.context.provider.AppContextCreator;
import org.cat.eye.common.context.provider.SpringContextProvider;
import org.cat.eye.common.context.provider.XmlAppContextCreator;
import org.cat.eye.common.context.provider.exception.CloseAppContextException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;

public class SpringContextProviderImpl implements SpringContextProvider {

    private final static Logger LOGGER = LoggerFactory.getLogger(SpringContextProviderImpl.class);

    private final ApplicationContext applicationContext;

    SpringContextProviderImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void close() throws CloseAppContextException {
        try {
            if (this.applicationContext instanceof DisposableBean) {
                ((DisposableBean) this.applicationContext).destroy();
            }
        } catch (Exception e) {
            throw new CloseAppContextException("SpringContextProviderImpl.close - " + e.getMessage(), e);
        }
    }

    public static void start(String ... config) {

        try {
            final SpringContextProvider context = createContext(config);

            final Thread mainThread = Thread.currentThread();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (context != null) {
                        context.close();
                    }
                } finally {
                    mainThread.interrupt();
                }
            }));

            while (!mainThread.isInterrupted()) {
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                    LOGGER.error("The main tread sleeping was interrupted.");
                }
            }
        } catch (Exception e) {
            System.exit(1);
        }
    }

    private static SpringContextProvider createContext(String ... config) throws Exception {

        AppContextCreator creator = null;

        if (config[0].endsWith(".xml")) {
            creator = new XmlAppContextCreatorImpl();
            ((XmlAppContextCreator) creator).addConfigLocation(config);
        } else if (config[0].endsWith(".class")) {
            for (String classConfig : config) {
                String configClassName = classConfig.substring(0, classConfig.indexOf(".class"));
                Class<?> clazz = Class.forName(configClassName);
                creator = new AnnotationAppContextCreatorImpl();
                ((AnnotationAppContextCreator) creator).addConfigLocation(clazz);
            }
        } else {
            throw new Exception("SpringContextProviderImpl.createContext - cannot create application container.");
        }

        return creator.createContext();
    }

}
