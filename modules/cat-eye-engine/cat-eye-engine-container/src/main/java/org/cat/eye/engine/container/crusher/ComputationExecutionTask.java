package org.cat.eye.engine.container.crusher;

import org.cat.eye.engine.container.crusher.computation.ComputationFactory;
import org.cat.eye.engine.container.deployment.management.Bundle;
import org.cat.eye.engine.container.model.Computation;
import org.cat.eye.engine.container.model.ComputationState;
import org.cat.eye.engine.container.model.MethodSpecification;
import org.cat.eye.engine.container.service.ComputationContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Kotov on 09.11.2017.
 */
public class ComputationExecutionTask implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ComputationExecutionTask.class);

    private Computation computation;

    private Bundle bundle;

    private ComputationContextService computationContextService;

    public ComputationExecutionTask(Computation computation, Bundle bundle, ComputationContextService computationContextService) {
        this.computation = computation;
        this.bundle = bundle;
        this.computationContextService = computationContextService;
    }

    @Override
    public void run() {
        // save current class loader
        ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
        // set bundle class loader
        Thread.currentThread().setContextClassLoader(bundle.getClassLoader());
        // get computable classes with them methods' specifications
        Map<Class<?>, Set<MethodSpecification>> computableClasses = bundle.getComputables();
        // get method specification for current computation
        Object computer = computation.getComputer();
        Set<MethodSpecification> methods = computableClasses.get(computer.getClass());
        // get computation next step method
        Optional<MethodSpecification> optional =
                methods.stream().filter(spec -> spec.getStep() == computation.getNextStep()).findFirst();
        // execute method if it is present
        if (optional.isPresent()) {
            try {
                List<?> computers;
                MethodSpecification specification = optional.get();
                Parameter[]  parameters = specification.getParameters();
                if (parameters != null && parameters.length != 0) {
                    // get parameters from service
                    Object[] args = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        Object arg = computationContextService.getArgument(parameters[i], bundle.getDomain());
                        if (arg == null) {
                            try {
                                arg = parameters[i].getType().newInstance();
                            } catch (InstantiationException e) {
                                String error = String.format("ComputationExecutionTask.run - " +
                                        "can't create parameter: computable class [%s], method [%s], type [%s].",
                                        computer.getClass().getName(), optional.get().getMethod(), parameters[i].getType());
                                LOGGER.error(error, e);
                                throw new RuntimeException(error, e);
                            }
                        }
                        args[i] = arg;
                    }
                    // invoke method with parameters
                    computers = (List<?>) optional.get().getMethod().invoke(computer, args);
                    // store output parameters by service
                    computationContextService.storeArguments(args, bundle.getDomain());
                } else {
                    // invoke method without parameters
                    computers = (List<?>) optional.get().getMethod().invoke(computer);
                }

                if (computers != null && !computers.isEmpty()) {
                    // set computation status
                    computation.setState(ComputationState.WAITING);
                    // create computations
                    List<Computation> childComputations = computers
                            .parallelStream()
                            .map(c -> ComputationFactory.create(c, computation.getId(), bundle.getDomain()))
                            .collect(Collectors.toList());
                    // register children in computation

                    // store computations by service

                    // update current computation state

                    // put new computations to queue
                    computationContextService.putCreatedComputationsToQueue(childComputations);

                } else {
                    // set computation status
                    computation.setState(ComputationState.READY);
                    // try to execute next step

                }

                // update parent computation state

                // update parent computation by service

                // set parent of these computations

                //


            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            computation.setNextStep(computation.getNextStep() + 1);

        } else {
            // mark computations as COMPLETED
            computation.setState(ComputationState.COMPLETED);
            // update computation in store
            computationContextService.storeComputation(computation);
        }


        // restore class loader
        Thread.currentThread().setContextClassLoader(currentCL);
    }
}
