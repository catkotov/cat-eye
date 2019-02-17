package org.cat.eye.engine.container.unit.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.msg.Message;
import org.cat.eye.engine.common.service.ComputationContextService;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Kotov on 08.02.2019.
 */
public class ComputationDriverUnit extends AbstractActor {

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
                .match(Message.NewComputation.class, newComputation ->
                    dispatcher.tell(
                        new Message.RunnableComputation(newComputation.getComputation()), getSelf())
                )
                .match(Message.CompletedComputation.class, completedComputation -> latch.countDown())
                .build();
    }
}
