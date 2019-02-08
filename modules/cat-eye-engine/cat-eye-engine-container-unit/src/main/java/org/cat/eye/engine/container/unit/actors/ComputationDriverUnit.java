package org.cat.eye.engine.container.unit.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.cat.eye.engine.common.model.Computation;

import java.util.ArrayList;
import java.util.List;

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

    private ActorRef dispatcher = getContext().actorOf(Props.create(ComputationDriverUnit.class, getSelf()));

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NewComputation.class, newComputation -> {
                    List<Computation> computations = new ArrayList<>(1);
                    computations.add(newComputation.getComputation());
                    dispatcher.tell(new ComputationDispatcherUnit.RunnableComputations(computations), getSelf());
                })
                .match(CompletedComputation.class, completedComputation -> {

                })
                .build();
    }
}
