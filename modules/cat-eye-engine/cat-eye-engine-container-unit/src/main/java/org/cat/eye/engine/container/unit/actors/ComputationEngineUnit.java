package org.cat.eye.engine.container.unit.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.cat.eye.engine.container.unit.crusher.AkkaComputationExecutor;

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

    private ComputationContextService computationContextService;

    private Bundle bundle;

    public ComputationEngineUnit(ActorRef driver,
                                 ActorRef dispatcher,
                                 ComputationContextService computationContextService,
                                 Bundle bundle) {
        this.driver = driver;
        this.dispatcher = dispatcher;
        this.computationContextService = computationContextService;
        this.bundle = bundle;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RunningComputation.class, runningComputation -> {
                    AkkaComputationExecutor executor =
                        new AkkaComputationExecutor(
                            runningComputation.getComputation(), bundle, computationContextService, dispatcher, driver, getSelf());
                    executor.run();
                })
                .build();
    }
}
