package org.cat.eye.engine.common.service.impl;

import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Kotov on 10.11.2017.
 */
public class SimpleComputationContextService implements ComputationContextService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleComputationContextService.class);

    private Map<UUID, Computation> computationStore = new ConcurrentHashMap<>();

    private Map<String, Object> argumentStore = new ConcurrentHashMap<>();

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

}
