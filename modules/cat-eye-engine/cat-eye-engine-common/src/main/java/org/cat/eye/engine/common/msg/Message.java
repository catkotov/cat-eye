package org.cat.eye.engine.common.msg;

import org.cat.eye.engine.common.model.Computation;

import java.io.Serializable;

public interface Message {
    // TODO customise serialisation of messages
    class NewComputation implements Serializable {

        private final Computation computation;

        public NewComputation(Computation computation) {
            this.computation = computation;
        }

        public Computation getComputation() {
            return this.computation;
        }
    }

    class CompletedComputation implements Serializable {

        private Computation computation;

        public CompletedComputation(Computation computation) {
            this.computation = computation;
        }

        public Computation getComputation() {
            return this.computation;
        }
    }

    class RunnableComputation implements Serializable {

        private final Computation computation;

        public RunnableComputation(Computation computations) {
            this.computation = computations;
        }

        public Computation getComputation() {
            return this.computation;
        }
    }

    class RunningComputation implements Serializable {

        private final Computation computation;

        public RunningComputation(Computation computation) {
            this.computation = computation;
        }

        public Computation getComputation() {
            return this.computation;
        }
    }
}
