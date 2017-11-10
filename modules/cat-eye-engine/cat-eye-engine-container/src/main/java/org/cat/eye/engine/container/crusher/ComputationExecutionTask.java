package org.cat.eye.engine.container.crusher;

import org.cat.eye.engine.container.crusher.computation.ComputationFactory;
import org.cat.eye.engine.container.deployment.management.Bundle;
import org.cat.eye.engine.container.model.Computation;
import org.cat.eye.engine.container.model.MethodSpecification;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Created by Kotov on 09.11.2017.
 */
public class ComputationExecutionTask implements Runnable {

    private Computation computation;

    private Bundle bundle;

    public ComputationExecutionTask(Computation computation, Bundle bundle) {
        this.computation = computation;
        this.bundle = bundle;
    }

    @Override
    public void run() {
        // save current class loader
        ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
        // set bundle class loader
        Thread.currentThread().setContextClassLoader(bundle.getClassLoader());

        Map<Class<?>, Set<MethodSpecification>> computables = bundle.getComputables();

        Object computer = computation.getComputer();
        Set<MethodSpecification> methods = computables.get(computer.getClass());




        Optional<MethodSpecification> optional =
                methods.stream().filter(spec -> spec.getStep() == computation.getNextStep()).findFirst();

        if (optional.isPresent()) {
            try {
                MethodSpecification specification = optional.get();
                Map<Parameter,Annotation> parameters = specification.getParameterAnnotationMap();
                if (parameters != null && !parameters.isEmpty()) {
                    // get parameters from service

                    // invoke method with parameters

                    // create computations

                    // store output parameters by service

                } else {
                    List<?> computers = (List<?>) optional.get().getMethod().invoke(computer);
                    // create computations
                }
                // set parent computation status

                // update parent computation by service

                // store computations by service

                // put new computations to queue

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            computation.setNextStep(computation.getNextStep() + 1);
        }



        // restore class loader
        Thread.currentThread().setContextClassLoader(currentCL);
    }
}
