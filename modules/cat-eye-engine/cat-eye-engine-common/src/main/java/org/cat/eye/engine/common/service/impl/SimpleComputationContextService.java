package org.cat.eye.engine.common.service.impl;

import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.model.ComputationState;
import org.cat.eye.engine.common.service.ComputationContextService;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Created by Kotov on 10.11.2017.
 */
public class SimpleComputationContextService implements ComputationContextService {

    private Map<UUID, Computation> computationStore = new ConcurrentHashMap<>();

    private Map<String, Object> argumentStore = new ConcurrentHashMap<>();

    private Map<UUID, Computation> runningComputationStore = new ConcurrentHashMap<>();

    private Lock lock = new ReentrantLock();

    @Override
    public void storeComputation(Computation computation) {
        this.computationStore.put(computation.getId(), computation);
    }

    @Override
    public Object getArgument(Parameter parameter, String domain) {
        return this.argumentStore.get(domain + "-" + parameter.getType().getName());
    }

    @Override
    public void setArgument(Parameter parameter, String domain, Object argument) {
        this.argumentStore.put(domain + "-" + parameter.getType().getName(), argument);
    }

    @Override
    public void storeArguments(Object[] args, String domain) {
        for (Object arg : args) {
            this.argumentStore.put(domain + "-" + arg.getClass().getName(), arg);
        }
    }

    @Override
    public boolean tryToRunComputation(Computation computation) {

        boolean result = false;

        try {
            lock.lock(); // distributed storage imitation

            if (!this.runningComputationStore.containsKey(computation.getId())) {
                updateComputationState(computation, ComputationState.RUNNING);
                this.computationStore.put(computation.getId(), computation);
                this.runningComputationStore.put(computation.getId(), computation);
                result = true;
            }
        } finally {
            lock.unlock();
        }

        return result;
    }

    @Override
    public Computation addCompletedChildIdAndRefresh(UUID parentId, Computation childComputation) {
        // get parent computation
        Computation parentComputation = this.computationStore.get(parentId);
        // add completed child computation to the parent computation
        parentComputation.addCompletedChildId(childComputation.getId());
        // update parent computation
        storeComputation(parentComputation);
        // return refreshed parent computation
        return getComputation(parentComputation.getId());
    }

    private Computation getComputation(UUID id) {
        return this.computationStore.get(id);
    }

    @Override
    public void fromRunningToWaiting(Computation computation) {
        // set computation status
        updateComputationState(computation, ComputationState.WAITING);
        // remove computation from running set
        removeRunningComputation(computation);
    }

    private void updateComputationState(Computation computation, ComputationState newState) {
        computation.setState(newState);
    }

    private void removeRunningComputation(Computation computation) {
        this.runningComputationStore.remove(computation.getId());
    }

    @Override
    public void registerChildrenComputations(Computation computation, List<Computation> childComputations) {
        // register children in computation
        List<UUID> childIDs = childComputations.stream().map(Computation::getId).collect(Collectors.toList());
        setChildrenComputationIds(computation, childIDs);
        // store computations by service
        storeComputations(childComputations);
        // set number of next step
        nextComputationStep(computation);
        // update current computation state
        storeComputation(computation);
    }

    private void setChildrenComputationIds(Computation computation, List<UUID> childIds) {
        computation.setChildrenIDs(childIds);
    }

    private void storeComputations(List<Computation> computations) {
        computations.forEach(this::storeComputation);
    }

    private void nextComputationStep(Computation computation) {
        computation.setNextStep(computation.getNextStep() + 1);
    }

    @Override
    public void fromRunningToReady(Computation computation) {
        // set computation status
        updateComputationState(computation, ComputationState.READY);
        // try to execute next step
        nextComputationStep(computation);
        // update current computation state
        removeRunningComputation(computation);
        storeComputation(computation);
    }

    @Override
    public void fromRunningToCompleted(Computation computation) {
        // mark computations as COMPLETED
        updateComputationState(computation, ComputationState.COMPLETED);
        // update computation in store
        storeComputation(computation);
        removeRunningComputation(computation);
    }

    @Override
    public void fromWaitingToReady(Computation computation) {
        updateComputationState(computation, ComputationState.READY);
        storeComputation(computation);
    }

    @Override
    public void close() {

    }
}
