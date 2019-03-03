package org.cat.eye.engine.common.crusher;

import akka.actor.ActorRef;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.msg.Message;
import org.cat.eye.engine.common.service.ComputationContextService;

/**
 * Created by Kotov on 09.02.2019.
 */
public class ComputationExecutorUnit extends AbstractComputationExecutor {

    private ActorRef dispatcher;

    private ActorRef driver;

    private ActorRef engine;

    public ComputationExecutorUnit(Computation computation,
                                   Bundle bundle,
                                   ComputationContextService computationContextService,
                                   ActorRef dispatcher, ActorRef driver, ActorRef engine) {

        super(computation, bundle, computationContextService);
        this.dispatcher = dispatcher;
        this.driver = driver;
        this.engine = engine;
    }

    protected void sendMsgToDispatcher(Computation computation) {
        dispatcher.tell(new Message.RunnableComputation(computation), engine);
    }

    protected void sendMsgToDriver(Computation computation) {
        driver.tell(new Message.CompletedComputation(computation), engine);
    }
}
