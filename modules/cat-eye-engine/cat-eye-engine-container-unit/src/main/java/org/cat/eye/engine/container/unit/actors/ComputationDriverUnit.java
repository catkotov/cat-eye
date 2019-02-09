package org.cat.eye.engine.container.unit.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.service.ComputationContextService;

/**
 * Created by Kotov on 08.02.2019.
 */
public class ComputationDriverUnit extends AbstractActor {

    public static class NewComputation {

        private final Computation computation;

        public NewComputation(Computation computation) {
            this.computation = computation;
        }

        Computation getComputation() {
            return this.computation;
        }
    }

    public static class CompletedComputation {

        private Computation computation;

        public CompletedComputation(Computation computation) {
            this.computation = computation;
        }

        public Computation getComputation() {
            return this.computation;
        }
    }

    private ComputationContextService computationContextService;

    private Bundle bundle;

    public ComputationDriverUnit(ComputationContextService computationContextService, Bundle bundle) {
        this.computationContextService = computationContextService;
        this.bundle = bundle;
    }

    private ActorRef dispatcher =
            getContext().actorOf(Props.create(ComputationDriverUnit.class, getSelf(), computationContextService, bundle));

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NewComputation.class, newComputation ->
                    dispatcher.tell(
                        new ComputationDispatcherUnit.RunnableComputation(newComputation.getComputation()), getSelf())
                )
                .match(CompletedComputation.class, completedComputation -> {

                })
                .build();
    }
}
