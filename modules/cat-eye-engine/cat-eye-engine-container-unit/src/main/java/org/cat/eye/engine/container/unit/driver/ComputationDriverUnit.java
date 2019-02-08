package org.cat.eye.engine.container.unit.driver;

import akka.actor.AbstractActor;
import org.cat.eye.engine.common.model.Computation;

/**
 * Created by Kotov on 08.02.2019.
 */
public class ComputationDriverUnit extends AbstractActor {

    public static class NewComputation {

        private final Computation computation;

        public NewComputation(Computation computation) {
            this.computation = computation;
        }

        public Computation getComputation() {
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

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NewComputation.class, newComputation -> {

                })
                .match(CompletedComputation.class, completedComputation -> {

                })
                .build();
    }
}
