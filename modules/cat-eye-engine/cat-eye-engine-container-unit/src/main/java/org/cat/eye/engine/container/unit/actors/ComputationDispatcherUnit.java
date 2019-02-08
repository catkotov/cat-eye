package org.cat.eye.engine.container.unit.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.SmallestMailboxPool;
import org.cat.eye.engine.common.model.Computation;

import java.util.List;

/**
 * Created by Kotov on 08.02.2019.
 */
public class ComputationDispatcherUnit extends AbstractActor {

    public static class RunnableComputations {

        private final List<Computation> computations;

        RunnableComputations(List<Computation> computations) {
            this.computations = computations;
        }

        public List<Computation> getComputations() {
            return this.computations;
        }
    }

    private ActorRef driver;

    ActorRef router = getContext().actorOf(
            new SmallestMailboxPool(8).props(Props.create(ComputationEngineUnit.class, driver, getSelf())));

    public ComputationDispatcherUnit(ActorRef driver) {
        this.driver = driver;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RunnableComputations.class, runnableComputations -> {

                })
                .build();
    }
}
