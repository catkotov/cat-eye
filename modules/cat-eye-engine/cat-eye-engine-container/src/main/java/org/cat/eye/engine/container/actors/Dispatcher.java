package org.cat.eye.engine.container.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.cluster.pubsub.DistributedPubSubSettings;
import akka.routing.SmallestMailboxRoutingLogic;
import org.cat.eye.engine.common.ContainerRole;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.model.ComputationState;
import org.cat.eye.engine.common.msg.Message;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.cat.eye.engine.common.util.CatEyeActorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cat.eye.engine.common.MsgTopic.*;

/**
 * Created by Kotov on 22.02.2019.
 */
public class Dispatcher extends AbstractActor {

    private final static Logger LOGGER = LoggerFactory.getLogger(Dispatcher.class);

    private ActorRef mediator;

    private String domain;

    private ComputationContextService computationContextService;

    public Dispatcher(String domain, ComputationContextService computationContextService) {

        this.computationContextService = computationContextService;

        this.domain = domain;

        DistributedPubSubSettings settings = DistributedPubSubSettings
                .create(getContext().system())
                .withRoutingLogic(SmallestMailboxRoutingLogic.apply())
                .withSendToDeadLettersWhenNoSubscribers(true);

        this.mediator = getContext().system().actorOf(Props.create(DistributedPubSubMediator.class, settings), domain);

        this.mediator.tell(
                new DistributedPubSubMediator.Subscribe(
                        CatEyeActorUtil.getTopicName(domain, RUNNABLE_COMPUTATION),
                        ContainerRole.DISPATCHER.getRole(),
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
                .match(Message.RunnableComputation.class, comp -> {
                        LOGGER.info("createReceive - RUNNABLE computation [" + comp.getComputation().getId() + "] was received.");
                        // change state of computation
                        Computation computation = comp.getComputation();
                        computation.setState(ComputationState.RUNNING);
                        // store computation into computation context service
                        computationContextService.storeComputation(computation);
                        // put computation into set of running computations
                        computationContextService.putRunningComputation(computation);
                        // create new running computation and send it to engine
                        this.mediator.tell(
                                new DistributedPubSubMediator.Publish(
                                        CatEyeActorUtil.getTopicName(domain, RUNNING_COMPUTATION),
                                        new Message.RunningComputation(comp.getComputation()),
                                        true
                                ),
                                getSelf()
                        );
                        LOGGER.info("createReceive - computation [" + comp.getComputation().getId() + "] was send to engine.");
                })
                .build();
    }
}
