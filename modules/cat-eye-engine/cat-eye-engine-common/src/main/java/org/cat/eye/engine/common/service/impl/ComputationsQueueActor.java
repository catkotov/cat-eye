package org.cat.eye.engine.common.service.impl;

import akka.actor.AbstractActor;
import org.cat.eye.engine.common.model.Computation;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ComputationsQueueActor extends AbstractActor {

    private Queue<Computation> executionQueue = new ConcurrentLinkedQueue<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ReadyComputation.class, readyComputation -> {
                        putReadyComputationToQueue(readyComputation.getComputation());
                        getSender().tell(Boolean.TRUE, getSelf());
                }).match(CreatedComputations.class, createdComputations -> {
                        putCreatedComputationsToQueue(createdComputations.getComputations());
                        getSender().tell(Boolean.TRUE, getSelf());
                }).match(TakeComputations.class, takeComputations -> {
                    List<Computation> computations = takeComputationsForExecution(takeComputations.getLimit());
                    if (computations == null) {
                        computations = Collections.emptyList();
                    }
                    getSender().tell(computations, getSelf());
                }).build();
    }

    public static class ReadyComputation {

        private Computation computation;

        public ReadyComputation(Computation computation) {
            this.computation = computation;
        }

        public Computation getComputation() {
            return computation;
        }
    }

    public static class CreatedComputations {

        private List<Computation> computations;

        public CreatedComputations(List<Computation> computations) {
            this.computations = computations;
        }

        List<Computation> getComputations() {
            return computations;
        }
    }

    public static class TakeComputations {

        private int limit;

        public TakeComputations(int limit) {
            this.limit = limit;
        }

        int getLimit() {
            return limit;
        }
    }

    private List<Computation> takeComputationsForExecution(int limit) {

        List<Computation> result = null;

        if (executionQueue.size() != 0) {

            result = new ArrayList<>();

            int compCount;

            if (limit <= executionQueue.size()) {
                compCount = limit;
            } else {
                compCount = executionQueue.size();
            }

            for (int i = 0; i < compCount; i++) {
                result.add(executionQueue.poll());
            }
        }

        return result;
    }

    private void putCreatedComputationsToQueue(List<Computation> computations) {
        executionQueue.addAll(computations);
    }

    private void putReadyComputationToQueue(Computation computation) {

        if (!executionQueue.contains(computation)) {
            executionQueue.add(computation);
        }
    }

}
