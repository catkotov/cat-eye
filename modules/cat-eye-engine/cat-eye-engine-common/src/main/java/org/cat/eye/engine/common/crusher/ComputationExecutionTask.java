package org.cat.eye.engine.common.crusher;

import org.cat.eye.engine.common.CatEyeContainerTaskCapacity;
import org.cat.eye.engine.common.crusher.computation.ComputationFactory;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.model.ComputationState;
import org.cat.eye.engine.common.model.MethodSpecification;
import org.cat.eye.engine.common.service.ComputationContextService;
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

    private final Computation computation;

    private final Bundle bundle;

    private final ComputationContextService computationContextService;

    private final CatEyeContainerTaskCapacity containerTaskCapacity;

    public ComputationExecutionTask(Computation computation,
                                    Bundle bundle,
                                    ComputationContextService computationContextService,
                                    CatEyeContainerTaskCapacity containerTaskCapacity) {
        this.computation = computation;
        this.bundle = bundle;
        this.computationContextService = computationContextService;
        this.containerTaskCapacity = containerTaskCapacity;
    }

    @Override
    public void run() {
        // save current class loader
        ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
        // set bundle class loader
        Thread.currentThread().setContextClassLoader(bundle.getClassLoader());
        // get computable classes with them methods' specifications
        Map<Class<?>, Set<MethodSpecification>> computableClasses = bundle.getComputables();

        try {
            // get methods' specification for current computation and execute next step (method)
            Object computer = computation.getComputer();
            Set<MethodSpecification> methods = computableClasses.get(computer.getClass());
            executeNextStep(computer, methods);
        } catch (Throwable t) {
            LOGGER.error("run - error reason: " + t.getMessage(), t);
            if (t instanceof Error) {
                throw (Error) t;
            }
        } finally {
            //
            containerTaskCapacity.release();
            // restore class loader
            Thread.currentThread().setContextClassLoader(currentCL);
        }
    }

    private void executeNextStep(Object computer,
                                 Set<MethodSpecification> methods) throws IllegalAccessException, InvocationTargetException {
        // get computation next step method
        Optional<MethodSpecification> optional =
                methods.stream().filter(spec -> spec.getStep() == computation.getNextStep()).findFirst();
        // execute method if it is present
        if (optional.isPresent()) {
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
                List<UUID> childIDs = childComputations.stream().map(Computation::getId).collect(Collectors.toList());
                computation.setChildrenIDs(childIDs);
                // store computations by service
                computationContextService.storeComputations(childComputations);
                // update current computation state
                computationContextService.storeComputation(computation);
                // put new computations to queue
                computationContextService.putCreatedComputationsToQueue(childComputations);
                // set number of next step
                computation.setNextStep(computation.getNextStep() + 1);
            } else {
                // set computation status
                computation.setState(ComputationState.READY);
                // try to execute next step
                computation.setNextStep(computation.getNextStep() + 1);
                // update current computation state
                computationContextService.storeComputation(computation);
                // call recursively this method
                executeNextStep(computer, methods);
            }
        } else {
            // mark computations as COMPLETED
            computation.setState(ComputationState.COMPLETED);
            // update computation in store
            computationContextService.storeComputation(computation);
            // try to update state of parent computation
            UUID parentId = computation.getParentId();
            Computation parentComputation = computationContextService.getComputation(parentId);
            if (parentComputation != null) {
                parentComputation.addCompletedChildId(computation.getId());
                computationContextService.storeComputation(parentComputation); // TODO double store (see line135)
                // put parent computation to queue if it is ready (has READY state)
                if (parentComputation.isChildrenCompleted()) {
                    parentComputation.setState(ComputationState.READY);
                    computationContextService.storeComputation(parentComputation); // TODO double store (see line 131)
                    computationContextService.putReadyComputationToQueue(parentComputation);
                }
            }
        }
    }

}
