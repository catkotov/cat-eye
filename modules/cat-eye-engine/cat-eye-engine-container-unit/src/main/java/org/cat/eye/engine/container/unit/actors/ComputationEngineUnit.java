package org.cat.eye.engine.container.unit.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import org.cat.eye.engine.common.model.Computation;

/**
 * Created by Kotov on 08.02.2019.
 */
public class ComputationEngineUnit extends AbstractActor {

    public static class RunningComputation {

        private final Computation computation;

        public RunningComputation(Computation computation) {
            this.computation = computation;
        }

        public Computation getComputation() {
            return this.computation;
        }
    }

    private ActorRef driver;

    private ActorRef dispatcher;

    public ComputationEngineUnit(ActorRef driver, ActorRef dispatcher) {
        this.driver = driver;
        this.dispatcher = dispatcher;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RunningComputation.class, runningComputation -> {

                })
                .build();
    }
}
