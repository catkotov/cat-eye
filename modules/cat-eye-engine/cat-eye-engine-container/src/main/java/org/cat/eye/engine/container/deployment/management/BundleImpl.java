package org.cat.eye.engine.container.deployment.management;

import org.cat.eye.engine.container.model.MethodSpecification;

import java.util.Map;
import java.util.Set;

/**
 * Created by Kotov on 29.10.2017.
 */
public class BundleImpl implements Bundle {

    private ClassLoader classLoader;

    private String domain;

    private Map<Class<?>, Set<MethodSpecification>> computables;

    public BundleImpl(String domain, Map<Class<?>, Set<MethodSpecification>> computables, ClassLoader classLoader) {
        this.domain = domain;
        this.computables = computables;
        this.classLoader = classLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public Map<Class<?>, Set<MethodSpecification>> getComputables() {
        return computables;
    }
}
