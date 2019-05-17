package org.cat.eye.engine.common.deployment.management;

import org.cat.eye.engine.common.model.MethodSpecification;
import org.cat.eye.engine.common.service.BundleService;
import org.cat.eye.engine.common.service.ComputationContextService;

import java.util.Map;
import java.util.Set;

/**
 * Created by Kotov on 29.10.2017.
 */
public class BundleImpl implements Bundle {

    private ClassLoader classLoader;

    private String domain;

    private Map<Class<?>, Set<MethodSpecification>> computables;

    private BundleService bundleService;

    private ComputationContextService computationContextService;

    public BundleImpl(String domain,
                      Map<Class<?>,Set<MethodSpecification>> computables,
                      ClassLoader classLoader,
                      BundleService bundleService,
                      ComputationContextService computationContextService) {
        this.domain = domain;
        this.computables = computables;
        this.classLoader = classLoader;
        this.bundleService = bundleService;
        this.computationContextService = computationContextService;
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

    @Override
    public BundleService getBundleService() {
        return bundleService;
    }

    @Override
    public ComputationContextService getComputationContextService() {
        return computationContextService;
    }
}
