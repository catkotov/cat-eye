package org.cat.eye.engine.container.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.cluster.pubsub.DistributedPubSubSettings;
import akka.routing.SmallestMailboxRoutingLogic;
import org.cat.eye.engine.common.msg.Message;
import org.cat.eye.engine.common.util.CatEyeActorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cat.eye.engine.common.MsgTopic.*;

/**
 * Created by Kotov on 21.02.2019.
 */
public class Driver extends AbstractActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);

    private ActorRef mediator;

    private String domain;

    public Driver(String domain) {

        this.domain = domain;

        DistributedPubSubSettings settings = DistributedPubSubSettings
                .create(getContext().system())
                .withRoutingLogic(SmallestMailboxRoutingLogic.apply())
                .withSendToDeadLettersWhenNoSubscribers(true);

        this.mediator = getContext().system().actorOf(Props.create(DistributedPubSubMediator.class, settings), domain);

        this.mediator.tell(
                new DistributedPubSubMediator.Subscribe(
                        CatEyeActorUtil.getTopicName(domain, NEW_COMPUTATION),
                        getSelf()
                ),
                getSelf()
        );

        this.mediator.tell(
                new DistributedPubSubMediator.Subscribe(
                        CatEyeActorUtil.getTopicName(domain, COMPLETED_COMPUTATION),
                        getSelf()
                ),
                getSelf()
        );
    }

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(DistributedPubSubMediator.SubscribeAck.class, msg ->
                    LOGGER.info("createReceive - was subscribed to topic: " + msg.subscribe().topic()))
                .match(Message.CompletedComputation.class, comp ->
                    LOGGER.info("createReceive - computation [" + comp.getComputation().getId() + "] was COMPLETED."))
                .match(Message.NewComputation.class, comp -> {
                    LOGGER.info("createReceive - NEW computation [" + comp.getComputation().getId() + "] was received.");
                    this.mediator.tell(
                            new DistributedPubSubMediator.Publish(
                                    CatEyeActorUtil.getTopicName(domain, RUNNABLE_COMPUTATION),
                                    new Message.RunnableComputation(comp.getComputation()),
                                    true
                            ),
                            getSelf()
                    );
                    LOGGER.info("createReceive - computation [" + comp.getComputation().getId() + "] was send to dispatcher.");
                })
                .build();

    }
}
