package org.cat.eye.engine.container.crusher;

import org.cat.eye.engine.container.crusher.computation.ComputationFactory;
import org.cat.eye.engine.container.deployment.management.Bundle;
import org.cat.eye.engine.container.model.Computation;
import org.cat.eye.engine.container.model.MethodSpecification;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by Kotov on 09.11.2017.
 */
public class ComputationExecutionTask implements Callable<List<Computation>> {

    private Computation computation;

    private Bundle bundle;

    public ComputationExecutionTask(Computation computation, Bundle bundle) {
        this.computation = computation;
        this.bundle = bundle;
    }

    @Override
    public List<Computation> call() {

        List<Computation> result = Collections.EMPTY_LIST;

        ClassLoader currentCL = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(bundle.getClassLoader());

        Map<Class<?>, Set<MethodSpecification>> computables = bundle.getComputables();

        Object comp = computation.getComputer();

        Set<MethodSpecification> methods = computables.get(comp.getClass());
        MethodSpecification method = methods.stream().filter(spec -> spec.getStep() == computation.getNextStep()).findAny().get();

        try {
            List<?> computers = (List<?>) method.getMethod().invoke(comp);



        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }


        Thread.currentThread().setContextClassLoader(currentCL);

        return result;
    }
}
