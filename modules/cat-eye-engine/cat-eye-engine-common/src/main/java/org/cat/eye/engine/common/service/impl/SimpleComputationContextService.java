package org.cat.eye.engine.common.service.impl;

import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.model.ComputationState;
import org.cat.eye.engine.common.service.ComputationContextService;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kotov on 10.11.2017.
 */
public class SimpleComputationContextService implements ComputationContextService {

    private Map<UUID, Computation> computationStore = new ConcurrentHashMap<>();

    private Map<String, Object> argumentStore = new ConcurrentHashMap<>();

    private Map<UUID, Computation> runningComputationStore = new ConcurrentHashMap<>();

    @Override
    public void storeComputations(List<Computation> computations) {
        computations.forEach(this::storeComputation);
    }

    @Override
    public void storeComputation(Computation computation) {
        computationStore.put(computation.getId(), computation);
    }

    @Override
    public Computation getComputation(UUID id) {
        return computationStore.get(id);
    }

    @Override
    public Object getArgument(Parameter parameter, String domain) {
        return argumentStore.get(domain + "-" + parameter.getType().getName());
    }

    @Override
    public void storeArguments(Object[] args, String domain) {
        for (Object arg : args) {
            argumentStore.put(domain + "-" + arg.getClass().getName(), arg);
        }
    }

    @Override
    public void putRunningComputation(Computation computation) {
        this.runningComputationStore.put(computation.getId(), computation);
    }

    @Override
    public void removeRunningComputation(Computation computation) {
        this.runningComputationStore.remove(computation.getId());
    }

    @Override
    public void updateComputationState(Computation computation, ComputationState newState) {
        computation.setState(newState);
    }

    @Override
    public void setChildrenComputationIds(Computation computation, List<UUID> childIds) {
        computation.setChildrenIDs(childIds);
    }

    @Override
    public void nextComputationStep(Computation computation) {
        computation.setNextStep(computation.getNextStep() + 1);
    }

    @Override
    public Computation addCompletedChildIdAndRefresh(UUID parentId, Computation childComputation) {
        // get parent computation
        Computation parentComputation = this.computationStore.get(parentId);
        // add completed child computation to the parent computation
        parentComputation.addCompletedChildId(childComputation.getId());
        // update parent computation
        this.storeComputation(parentComputation);
        // return refreshed parent computation
        return this.getComputation(parentComputation.getId());
    }
}
