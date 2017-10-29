package org.cat.eye.engine.container.deployment.management;

import org.cat.eye.engine.container.model.MethodSpecification;

import java.util.Map;
import java.util.Set;

/**
 * Created by Kotov on 21.10.2017.
 */
public interface Bundle {

    ClassLoader getClassLoader();

    String getDomain();

    Map<Class<?>, Set<MethodSpecification>> getComputables();
}
