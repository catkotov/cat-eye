package org.cat.eye.engine.common.service;

import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.model.ComputationState;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.UUID;

public interface ComputationContextService {

    void storeComputations(List<Computation> computations);

    void storeComputation(Computation computation);

    Computation getComputation(UUID id);

    Object getArgument(Parameter parameter, String domain);

    void storeArguments(Object[] args, String domain);

    boolean tryToRunComputation(Computation computation);

    void removeRunningComputation(Computation computation);

    void updateComputationState(Computation computation, ComputationState newState);

    void setChildrenComputationIds(Computation computation, List<UUID> childIds);

    void nextComputationStep(Computation computation);

    Computation addCompletedChildIdAndRefresh(UUID parentId, Computation childComputation);
}
