package org.cat.eye.engine.container.context;

import akka.actor.ActorSystem;
import org.cat.eye.engine.common.deployment.management.Bundle;

/**
 * Created by Kotov on 27.02.2019.
 */
public class DomainContext {

    private Bundle bundle;

    private ActorSystem system;

    private String domain;

    public DomainContext(String domain, Bundle bundle, ActorSystem system) {
        this.domain = domain;
        this.bundle = bundle;
        this.system = system;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public ActorSystem getSystem() {
        return system;
    }

    public String getDomain() {
        return domain;
    }
}
