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

    private Lock lock = new ReentrantLock();

    private Map<UUID, Computation> computationStore = new ConcurrentHashMap<>();

    private Queue<Computation> executionQueue = new ConcurrentLinkedQueue<>();

    private Map<String, Object> argumentStore = new ConcurrentHashMap<>();

    @Override
    public List<Computation> takeComputationsForExecution(int limit) {

        List<Computation> result = null;

        if (executionQueue.size() != 0) {

            lock.lock();

            result = new ArrayList<>();
            try {
                int compCount;

                if (limit <= executionQueue.size()) {
                    compCount = limit;
                } else {
                    compCount = executionQueue.size();
                }

                for (int i = 0; i < compCount; i++) {
                    result.add(executionQueue.poll());
                }
            } finally {
                lock.unlock();
            }
        }

        return result;
    }

    @Override
    public void putCreatedComputationsToQueue(List<Computation> computations) {
        executionQueue.addAll(computations);
    }

    @Override
    public void putReadyComputationToQueue(Computation computation) {

        lock.lock();

        try {
            if (!executionQueue.contains(computation)) {
                executionQueue.add(computation);
            } else {
                LOGGER.info("++++++++ It's double computation +++++++");
            }
        } finally {
            lock.unlock();
        }
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
