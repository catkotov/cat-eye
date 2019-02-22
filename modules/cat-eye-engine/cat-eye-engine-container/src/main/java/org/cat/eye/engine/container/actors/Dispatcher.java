package org.cat.eye.engine.container.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.cluster.pubsub.DistributedPubSubSettings;
import akka.routing.SmallestMailboxRoutingLogic;
import org.cat.eye.engine.common.ContainerRole;
import org.cat.eye.engine.common.msg.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cat.eye.engine.common.MsgTopic.*;

/**
 * Created by Kotov on 22.02.2019.
 */
public class Dispatcher extends AbstractActor {

    private final static Logger LOGGER = LoggerFactory.getLogger(Dispatcher.class);

    public Dispatcher() {

        DistributedPubSubSettings settings = DistributedPubSubSettings
                .create(getContext().system())
                .withRoutingLogic(SmallestMailboxRoutingLogic.apply())
                .withRole(ContainerRole.DISPATCHER.getRole())
                .withSendToDeadLettersWhenNoSubscribers(true);

        ActorRef mediator = getContext().system().actorOf(Props.create(DistributedPubSubMediator.class, settings));

        mediator.tell(new DistributedPubSubMediator.Subscribe(RUNNABLE_COMPUTATION.getTopicName(), getSelf()), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DistributedPubSubMediator.SubscribeAck.class, msg ->
                        LOGGER.info("createReceive - was subscribed to topic: " + msg.subscribe().topic()))
                .match(Message.RunnableComputation.class, msg -> {

                })
                .build();
    }
}
