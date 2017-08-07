package org.cat.eye.common.context.provider.impl;

import org.cat.eye.common.context.provider.AnnotationAppContextCreator;
import org.cat.eye.common.context.provider.SpringContextProvider;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class AnnotationAppContextCreatorImpl implements AnnotationAppContextCreator {

    private ApplicationContext parentContext;

    private ClassLoader classLoader;

    private List<Class<?>> annotatedClasses = new ArrayList<>();

    @Override
    public void addConfigLocation(Class<?> annotatedClass) throws Exception {
        this.annotatedClasses.add(annotatedClass);
    }

    @Override
    public SpringContextProvider createContext() throws Exception {

        Class<?>[] configClasses = this.annotatedClasses.toArray(new Class<?>[this.annotatedClasses.size()]);

        AnnotationApplicationContext applicationContext = new AnnotationApplicationContext(configClasses);

        if (this.classLoader != null) {
            applicationContext.setClassLoader(this.classLoader);
        }

//        applicationContext.refresh();

        return new SpringContextProviderImpl(applicationContext);
    }
}
