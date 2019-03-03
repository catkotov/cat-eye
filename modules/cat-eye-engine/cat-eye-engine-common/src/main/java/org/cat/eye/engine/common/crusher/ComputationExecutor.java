package org.cat.eye.engine.common.crusher;

import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSubMediator;
import org.cat.eye.engine.common.MsgTopic;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.msg.Message;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.cat.eye.engine.common.util.CatEyeActorUtil;

/**
 * Created by Kotov on 09.02.2019.
 */
public class ComputationExecutor extends AbstractComputationExecutor {

    private ActorRef mediator;

    private ActorRef engine;

    public ComputationExecutor(Computation computation,
                               Bundle bundle,
                               ComputationContextService computationContextService,
                               ActorRef mediator,
                               ActorRef engine) {

        super(computation, bundle, computationContextService);
        this.mediator = mediator;
        this.engine = engine;
    }

    protected void sendMsgToDispatcher(Computation computation) {
        mediator.tell(
                new DistributedPubSubMediator.Publish(
                        CatEyeActorUtil.getTopicName(bundle.getDomain(), MsgTopic.RUNNABLE_COMPUTATION),
                        new Message.RunnableComputation(computation),
                        true
                ),
                engine
        );
    }

    protected void sendMsgToDriver(Computation computation) {
        mediator.tell(
                new DistributedPubSubMediator.Publish(
                        CatEyeActorUtil.getTopicName(bundle.getDomain(), MsgTopic.COMPLETED_COMPUTATION),
                        new Message.CompletedComputation(computation)
                ),
                engine
        );
    }
}
