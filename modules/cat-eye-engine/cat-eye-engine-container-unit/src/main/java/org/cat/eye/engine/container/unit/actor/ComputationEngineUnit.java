package org.cat.eye.engine.container.unit.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import org.cat.eye.engine.common.crusher.ComputationExecutor;
import org.cat.eye.engine.common.deployment.management.Bundle;
import org.cat.eye.engine.common.msg.Message;
import org.cat.eye.engine.common.service.ComputationContextService;

/**
 * Created by Kotov on 08.02.2019.
 */
public class ComputationEngineUnit extends AbstractActor {

    private ActorRef driver;

    private ActorRef dispatcher;

    private ComputationContextService computationContextService;

    private Bundle bundle;

    public ComputationEngineUnit(ActorRef driver,
                                 ActorRef dispatcher,
                                 ComputationContextService computationContextService,
                                 Bundle bundle) {
        this.driver = driver;
        this.dispatcher = dispatcher;
        this.computationContextService = computationContextService;
        this.bundle = bundle;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.RunningComputation.class, runningComputation -> {
                    ComputationExecutor executor =
                        new ComputationExecutor(
                            runningComputation.getComputation(), bundle, computationContextService, dispatcher, driver, getSelf());
                    executor.run();
                })
                .build();
    }
}
