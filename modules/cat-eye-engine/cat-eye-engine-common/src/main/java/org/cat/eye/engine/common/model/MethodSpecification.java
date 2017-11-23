package org.cat.eye.engine.common.model;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Created by Kotov on 27.10.2017.
 */
public class MethodSpecification implements Comparable<MethodSpecification> {

    private final int step;

    private final Method method;

    private final Parameter[] parameters;

    public MethodSpecification(Method method, int step, Parameter[] parameters) {
        this.method = method;
        this.step = step;
        this.parameters = parameters;
    }

    public int getStep() {
        return step;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public int compareTo(MethodSpecification o) {
        return Integer.compare(this.step, o.step);
    }
}
