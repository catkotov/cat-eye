package org.cat.eye.engine.container.service.impl;

import org.cat.eye.engine.container.model.Computation;
import org.cat.eye.engine.container.service.ComputationContextService;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Created by Kotov on 10.11.2017.
 */
public class SimpleComputationContextService implements ComputationContextService {

    private Lock lock = new ReentrantLock();

    private Map<UUID, Computation> computationStore = new ConcurrentHashMap<>();

    private Queue<Computation> executionQueue = new ConcurrentLinkedQueue<>();

    private Map<String, Object> argumentStore = new ConcurrentHashMap<>();

    @Override
    public List<Computation> takeComputationsForExecution(int limit) {

        lock.lock();

        List<Computation> result;
        try {
            result = executionQueue.stream().limit(limit).collect(Collectors.toList());
        } finally {
            lock.unlock();
        }

        return result;
    }

    @Override
    public void putCreatedComputationsToQueue(List<Computation> computations) {
        executionQueue.addAll(computations);
    }

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
