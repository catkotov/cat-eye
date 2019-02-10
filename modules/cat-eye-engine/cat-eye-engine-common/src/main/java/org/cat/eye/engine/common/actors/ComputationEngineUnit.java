package org.cat.eye.engine.common.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import org.cat.eye.engine.common.crusher.AkkaComputationExecutor;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.service.ComputationContextService;

/**
 * Created by Kotov on 08.02.2019.
 */
public class ComputationEngineUnit extends AbstractActor {

    static class RunningComputation {

        private final Computation computation;

        RunningComputation(Computation computation) {
            this.computation = computation;
        }

        Computation getComputation() {
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
