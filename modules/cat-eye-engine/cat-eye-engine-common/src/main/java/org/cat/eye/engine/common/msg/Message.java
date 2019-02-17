package org.cat.eye.engine.common.msg;

import org.cat.eye.engine.common.model.Computation;

public interface Message {

    class NewComputation {

        private final Computation computation;

        public NewComputation(Computation computation) {
            this.computation = computation;
        }

        public Computation getComputation() {
            return this.computation;
        }
    }

    class CompletedComputation {

        private Computation computation;

        public CompletedComputation(Computation computation) {
            this.computation = computation;
        }

        public Computation getComputation() {
            return this.computation;
        }
    }

    class RunnableComputation {

        private final Computation computation;

        public RunnableComputation(Computation computations) {
            this.computation = computations;
        }

        public Computation getComputation() {
            return this.computation;
        }
    }

    class RunningComputation {

        private final Computation computation;

        public RunningComputation(Computation computation) {
            this.computation = computation;
        }

        public Computation getComputation() {
            return this.computation;
        }
    }
}
