package org.cat.eye.engine.common.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.service.ComputationContextService;
import java.util.concurrent.CountDownLatch;

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

    private CountDownLatch latch;

    private ActorRef dispatcher;

    public ComputationDriverUnit(ComputationContextService computationContextService, Bundle bundle, CountDownLatch latch) {
        this.latch = latch;
        this.dispatcher = getContext().actorOf(
                Props.create(ComputationDispatcherUnit.class, getSelf(), computationContextService, bundle), "dispatcher");
    }



    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NewComputation.class, newComputation ->
                    dispatcher.tell(
                        new ComputationDispatcherUnit.RunnableComputation(newComputation.getComputation()), getSelf())
                )
                .match(CompletedComputation.class, completedComputation -> latch.countDown())
                .build();
    }
}
