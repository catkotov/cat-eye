package org.cat.eye.engine.container.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSubMediator;
import org.cat.eye.engine.common.ContainerRole;
import org.cat.eye.engine.common.crusher.ComputationExecutor;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.msg.Message;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.cat.eye.engine.common.util.CatEyeActorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cat.eye.engine.common.MsgTopic.*;

/**
 * Created by Kotov on 22.02.2019.
 */
public class Engine extends AbstractActor {

    private final static Logger LOGGER = LoggerFactory.getLogger(Engine.class);

    private ActorRef mediator;

    private Bundle bundle;

    private ComputationContextService computationContextService;

    public Engine(String domain, Bundle bundle, ComputationContextService computationContextService, ActorRef mediator) {

        this.computationContextService = computationContextService;
        this.bundle = bundle;
        this.mediator = mediator;

        this.mediator.tell(
                new DistributedPubSubMediator.Subscribe(
                        CatEyeActorUtil.getTopicName(domain, RUNNING_COMPUTATION),
                        ContainerRole.ENGINE.getRole(),
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
                .match(Message.RunningComputation.class, comp -> {
                        LOGGER.info("createReceive - computation [" + comp.getComputation().getId() + "] was received.");
                    ComputationExecutor executor =
                            new ComputationExecutor(
                                    comp.getComputation(), bundle, computationContextService, this.mediator, getSelf());
                    executor.run();
                })
                .build();
    }
}
