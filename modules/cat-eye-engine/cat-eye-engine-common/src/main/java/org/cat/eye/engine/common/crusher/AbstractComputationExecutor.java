package org.cat.eye.engine.common.crusher;

import org.cat.eye.engine.common.crusher.computation.ComputationFactory;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.model.ComputationState;
import org.cat.eye.engine.common.model.MethodSpecification;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Kotov on 03.03.2019.
 */
public abstract class AbstractComputationExecutor {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractComputationExecutor.class);

    private Computation computation;

    protected Bundle bundle;

    private ComputationContextService computationContextService;

    AbstractComputationExecutor(Computation computation,
                                Bundle bundle,
                                ComputationContextService computationContextService) {

        this.computation = computation;
        this.bundle = bundle;
        this.computationContextService = computationContextService;
    }

    public void run () {
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
            // restore class loader
            Thread.currentThread().setContextClassLoader(currentCL);
        }
    }

    private void executeNextStep(Object computer, Set<MethodSpecification> methods)
            throws Exception {
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
                    Object arg = bundle.getBundleService().getArgument(parameters[i], bundle.getDomain());
                    if (arg == null) {
                        try {
                            arg = parameters[i].getType().newInstance();
                        } catch (InstantiationException e) {
                            String error = String.format("run - " +
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
                bundle.getBundleService().storeArguments(args, bundle.getDomain());
            } else {
                // invoke method without parameters
                computers = (List<?>) optional.get().getMethod().invoke(computer);
            }

            if (computers != null && !computers.isEmpty()) {
                // set computation status WAITING and remove computation from running set
                computationContextService.fromRunningToWaiting(computation);
                // create computations
                List<Computation> childComputations = computers
                        .parallelStream()
                        .map(c -> ComputationFactory.create(c, computation.getId(), bundle.getDomain()))
                        .collect(Collectors.toList());
                // register children in computation
                computationContextService.registerChildrenComputations(computation, childComputations);
                // put new computations to queue
                childComputations.forEach(this::sendMsgToDispatcher);
            } else {
                // move computation from RUNNING state to READY state
                computationContextService.fromRunningToReady(computation);
                // update local value of computation
                computation = computationContextService.getComputation(computation.getId());
                // call recursively this method
                executeNextStep(computer, methods);
            }

        } else {
            // move computation from RUNNING state to COMPLETED state
            computationContextService.fromRunningToCompleted(computation);
            // try to update state of parent computation
            UUID parentId = computation.getParentId();
            if (parentId != null) {
                // add to parent completed child computation and refresh parent computation
                Computation parentComputation =
                        computationContextService.addCompletedChildIdAndRefresh(parentId, computation);
                // put parent computation to queue if it is ready (has READY state)
                if (parentComputation.isChildrenCompleted() && parentComputation.getState() == ComputationState.WAITING) {
                    // move parent computation from WAITING state to READY state
                    computationContextService.fromWaitingToReady(parentComputation);
                    // put ready computation to queue
                    sendMsgToDispatcher(parentComputation);
                }

            } else {
                sendMsgToDriver(computation);
            }
        }
    }

    protected abstract void sendMsgToDispatcher(Computation computation);

    protected abstract void sendMsgToDriver(Computation computation);
}
