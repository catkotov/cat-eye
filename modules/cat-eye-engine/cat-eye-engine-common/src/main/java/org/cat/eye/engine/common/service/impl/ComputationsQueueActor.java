package org.cat.eye.engine.common.service.impl;

import akka.actor.AbstractActor;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ComputationsQueueActor extends AbstractActor {

    @Autowired
    private ComputationContextService contextService;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ReadyComputation.class, readyComputation ->
                        contextService.putReadyComputationToQueue(readyComputation.getComputation()))
                .match(CreatedComputations.class, createdComputations ->
                        contextService.putCreatedComputationsToQueue(createdComputations.getComputations()))
                .match(TakeComputations.class, takeComputations -> {
                    List<Computation> computations = contextService.takeComputationsForExecution(takeComputations.getLimit());
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

        public List<Computation> getComputations() {
            return computations;
        }
    }

    public static class TakeComputations {

        private int limit;

        public TakeComputations(int limit) {
            this.limit = limit;
        }

        public int getLimit() {
            return limit;
        }
    }
}
