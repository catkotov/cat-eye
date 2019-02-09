package org.cat.eye.engine.common.service;

import org.cat.eye.engine.common.model.Computation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.UUID;

public interface ComputationContextService {

    void storeComputations(List<Computation> computations);

    void storeComputation(Computation computation);

    Computation getComputation(UUID id);

    Object getArgument(Parameter parameter, String domain);

    void storeArguments(Object[] args, String domain);

    default void putRunningComputation(Computation computation) {}

    default void removeRunningComputation(Computation computation) {}
}
