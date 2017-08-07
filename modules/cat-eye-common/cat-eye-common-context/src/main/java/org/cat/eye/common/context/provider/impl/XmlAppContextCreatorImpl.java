package org.cat.eye.common.context.provider.impl;

import org.cat.eye.common.context.provider.SpringContextProvider;
import org.cat.eye.common.context.provider.XmlAppContextCreator;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlAppContextCreatorImpl implements XmlAppContextCreator {

    private ApplicationContext parentContext;

    private ClassLoader classLoader;

    private List<Resource> configResources = new ArrayList<>();

    @Override
    public void addConfigLocation(String configPath) throws IOException {
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(configPath);
        for (Resource resource : resources) {
            addConfigLocation(resource);
        }
    }

    @Override
    public void addConfigLocation(Resource resource) throws IOException {
        this.configResources.add(resource);
    }

    @Override
    public SpringContextProvider createContext() throws Exception {
        Resource[] resources = this.configResources.toArray(new Resource[this.configResources.size()]);

        ResourceApplicationContext applicationContext = new ResourceApplicationContext(resources, this.parentContext);

        if (this.classLoader != null) {
            applicationContext.setClassLoader(this.classLoader);
        }

        applicationContext.refresh();

        return new SpringContextProviderImpl(applicationContext);
    }

    public void setParentContext(ApplicationContext parentContext) {
        this.parentContext = parentContext;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
