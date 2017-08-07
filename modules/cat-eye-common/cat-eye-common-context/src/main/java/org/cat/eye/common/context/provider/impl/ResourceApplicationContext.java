package org.cat.eye.common.context.provider.impl;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

public class ResourceApplicationContext extends AbstractXmlApplicationContext {

    private Resource[] resources;

    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public ResourceApplicationContext(Resource[] resources, ApplicationContext parent) {
        super(parent);
        this.resources = resources;
    }

    @Override
    protected Resource[] getConfigResources() {
        return this.resources;
    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.add(beanPostProcessor);
    }

    @Override
    protected DefaultListableBeanFactory createBeanFactory() {

        DefaultListableBeanFactory beanFactory = super.createBeanFactory();

        for (BeanPostProcessor beanPostProcessor : this.beanPostProcessors) {
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }

        return beanFactory;
    }
}
