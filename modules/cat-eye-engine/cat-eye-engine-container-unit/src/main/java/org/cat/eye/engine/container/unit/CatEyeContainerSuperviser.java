package org.cat.eye.engine.container.unit;

import akka.actor.AbstractLoggingActor;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;

/**
 * Created by Kotov on 15.01.2019.
 */
public class CatEyeContainerSuperviser extends AbstractLoggingActor {


    private SupervisorStrategy strategy = new OneForOneStrategy(false, DeciderBuilder
            .match(Exception.class, e -> {
                log().warning("Exception was rose: {0}. Actor {1} will be restarted.", e.getMessage(), sender().path().name());
                return SupervisorStrategy.restart();
            }).build()
    );

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public Receive createReceive() {
        return null;
    }
}
