package org.cat.eye.engine.container.service;

import org.cat.eye.engine.container.model.Computation;
import java.util.List;
import java.util.UUID;

public interface ComputationContextService {

    List<Computation> takeComputationsForExecution(int limit);

    void putCreatedComputationsToQueue(List<Computation> computations);

    void storeComputations(List<Computation> computations);

    void storeComputation(Computation computation);

    Computation getComputation(UUID id);


}
