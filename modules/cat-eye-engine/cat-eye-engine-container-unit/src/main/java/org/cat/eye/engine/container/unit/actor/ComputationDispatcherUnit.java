package org.cat.eye.engine.container.unit.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.SmallestMailboxPool;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.model.ComputationState;
import org.cat.eye.engine.common.msg.Message;
import org.cat.eye.engine.common.service.ComputationContextService;

/**
 * Created by Kotov on 08.02.2019.
 */
public class ComputationDispatcherUnit extends AbstractActor {

    private ComputationContextService computationContextService;

    private ActorRef router;

    public ComputationDispatcherUnit(ActorRef driver, ComputationContextService computationContextService, Bundle bundle) {
        this.computationContextService = computationContextService;
        // TODO make pool size configurable
        this.router = getContext().actorOf(
                new SmallestMailboxPool(8).props(Props
                                .create(ComputationEngineUnit.class, driver, getSelf(), computationContextService, bundle)
                                .withDispatcher("my-pinned-dispatcher")), "engine");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.RunnableComputation.class, runnableComputation -> {
                    Computation computation = runnableComputation.getComputation();
                    computation.setState(ComputationState.RUNNING);
                    computationContextService.storeComputation(computation);
                    if (computationContextService.tryToRunComputation(computation)) {
                        router.tell(new Message.RunningComputation(computation), getSelf());
                    }
                })
                .build();
    }
}
