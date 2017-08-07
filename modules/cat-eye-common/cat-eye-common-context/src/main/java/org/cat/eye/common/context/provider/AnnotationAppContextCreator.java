package org.cat.eye.common.context.provider;

public interface AnnotationAppContextCreator extends AppContextCreator {

    void addConfigLocation(Class<?> annotatedClass) throws Exception;

}
