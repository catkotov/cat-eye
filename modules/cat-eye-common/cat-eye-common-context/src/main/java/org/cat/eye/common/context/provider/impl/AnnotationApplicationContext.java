package org.cat.eye.common.context.provider.impl;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class AnnotationApplicationContext extends AnnotationConfigApplicationContext {

    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public AnnotationApplicationContext(Class<?>... annotatedClasses) {
        super(annotatedClasses);
    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.add(beanPostProcessor);
    }

    protected DefaultListableBeanFactory createBeanFactory() {

        DefaultListableBeanFactory beanFactory = super.getDefaultListableBeanFactory();

        for (BeanPostProcessor beanPostProcessor : this.beanPostProcessors) {
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }

        return beanFactory;
    }
}
