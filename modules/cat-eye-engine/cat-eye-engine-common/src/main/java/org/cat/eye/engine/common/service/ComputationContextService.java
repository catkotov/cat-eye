package org.cat.eye.engine.common.service;

import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.model.ComputationState;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.UUID;

public interface ComputationContextService {

    void storeComputation(Computation computation);

    Object getArgument(Parameter parameter, String domain);

    void storeArguments(Object[] args, String domain);

    boolean tryToRunComputation(Computation computation);

    Computation addCompletedChildIdAndRefresh(UUID parentId, Computation childComputation);

    void fromRunningToWaiting(Computation computation);

    void registerChildrenComputations(Computation computation, List<Computation> childComputations);

    void fromRunningToReady(Computation computation);

    void fromRunningToCompleted(Computation computation);

    void fromWaitingToReady(Computation computation);

    void close();

}
