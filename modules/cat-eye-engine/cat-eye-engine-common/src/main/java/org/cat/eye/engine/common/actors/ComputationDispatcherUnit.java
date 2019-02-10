package org.cat.eye.engine.common.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.SmallestMailboxPool;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.model.ComputationState;
import org.cat.eye.engine.common.service.ComputationContextService;

/**
 * Created by Kotov on 08.02.2019.
 */
public class ComputationDispatcherUnit extends AbstractActor {

    public static class RunnableComputation {

        private final Computation computation;

        public RunnableComputation(Computation computations) {
            this.computation = computations;
        }

        Computation getComputation() {
            return this.computation;
        }
    }

    private ComputationContextService computationContextService;

    private ActorRef router;

    public ComputationDispatcherUnit(ActorRef driver, ComputationContextService computationContextService, Bundle bundle) {
        this.computationContextService = computationContextService;

        this.router = getContext().actorOf(
                new SmallestMailboxPool(8).props(Props
                                .create(ComputationEngineUnit.class, driver, getSelf(), computationContextService, bundle)
                                .withDispatcher("my-pinned-dispatcher")), "engine");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RunnableComputation.class, runnableComputation -> {
                    Computation computation = runnableComputation.getComputation();
                    computation.setState(ComputationState.RUNNING);
                    computationContextService.storeComputation(computation);
                    computationContextService.putRunningComputation(computation);
                    router.tell(new ComputationEngineUnit.RunningComputation(computation), getSelf());
                })
                .build();
    }
}
