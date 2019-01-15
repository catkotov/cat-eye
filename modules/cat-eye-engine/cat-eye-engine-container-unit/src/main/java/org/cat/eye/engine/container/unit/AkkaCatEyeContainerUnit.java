package org.cat.eye.engine.container.unit;

import akka.actor.ActorSystem;

/**
 * Created by Kotov on 28.10.2018.
 */
public class AkkaCatEyeContainerUnit {

    private ActorSystem actorSystem = ActorSystem.create("cat-eye-container-unit-actor-system");

    public void initialize() {

    }
}
