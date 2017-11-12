package org.cat.eye.engine.container.crusher;

import org.cat.eye.engine.container.deployment.management.Bundle;
import org.cat.eye.engine.container.model.Computation;
import org.cat.eye.engine.container.model.MethodSpecification;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Created by Kotov on 09.11.2017.
 */
public class ComputationExecutionTask implements Runnable {

    private Computation parentComputation;

    private Bundle bundle;

    public ComputationExecutionTask(Computation computation, Bundle bundle) {
        this.parentComputation = computation;
        this.bundle = bundle;
    }

    @Override
    public void run() {
        // save current class loader
        ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
        // set bundle class loader
        Thread.currentThread().setContextClassLoader(bundle.getClassLoader());
        // get computable classes with them methods' specifications
        Map<Class<?>, Set<MethodSpecification>> computableClasses = bundle.getComputables();
        // get method specification for current parentComputation
        Object computer = parentComputation.getComputer();
        Set<MethodSpecification> methods = computableClasses.get(computer.getClass());
        // get parentComputation next step method
        Optional<MethodSpecification> optional =
                methods.stream().filter(spec -> spec.getStep() == parentComputation.getNextStep()).findFirst();
        // execute method if it is present
        if (optional.isPresent()) {
            try {
                MethodSpecification specification = optional.get();
                Parameter[]  parameters = specification.getParameters();
                if (parameters != null && parameters.length != 0) {
                    // get parameters from service

                    // invoke method with parameters

                    // create computations

                    // store output parameters by service

                } else {
                    List<?> computers = (List<?>) optional.get().getMethod().invoke(computer);
                    // create computations

                }


                // set parent parentComputation status

                // update parent parentComputation by service

                // set parent of these computations

                // store computations by service

                // put new computations to queue

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            parentComputation.setNextStep(parentComputation.getNextStep() + 1);

        } else {
            // mark computations as COMPLETED

            // update parentComputation in store

        }



        // restore class loader
        Thread.currentThread().setContextClassLoader(currentCL);
    }
}
