package org.cat.eye.engine.common.deployment.management;

import org.cat.eye.engine.common.model.MethodSpecification;
import org.cat.eye.engine.common.service.BundleService;
import org.cat.eye.engine.common.service.ComputationContextService;

import java.util.Map;
import java.util.Set;

/**
 * Created by Kotov on 21.10.2017.
 */
public interface Bundle {

    ClassLoader getClassLoader();

    String getDomain();

    Map<Class<?>, Set<MethodSpecification>> getComputables();

    BundleService getBundleService();

    ComputationContextService getComputationContextService();
}
