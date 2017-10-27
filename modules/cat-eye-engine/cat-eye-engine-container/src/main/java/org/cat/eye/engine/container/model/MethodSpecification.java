package org.cat.eye.engine.container.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * Created by Kotov on 27.10.2017.
 */
public class MethodSpecification implements Comparable<MethodSpecification> {

    private final int step;

    private final Map<Parameter, Annotation> parameterAnnotationMap;

    private final Method method;

    public MethodSpecification(Method method, int step, Map<Parameter, Annotation> parameterAnnotationMap) {
        this.method = method;
        this.step = step;
        this.parameterAnnotationMap = parameterAnnotationMap;
    }

    public int getStep() {
        return step;
    }

    public Map<Parameter, Annotation> getParameterAnnotationMap() {
        return parameterAnnotationMap;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public int compareTo(MethodSpecification o) {
        return Integer.compare(this.step, o.step);
    }
}
