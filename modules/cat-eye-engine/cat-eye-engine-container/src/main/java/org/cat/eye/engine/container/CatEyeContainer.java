package org.cat.eye.engine.container;

import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by Kotov on 18.02.2019.
 */
public class CatEyeContainer {

    private Config config;

    private ActorSystem system;

    public CatEyeContainer() {

        this.config = ConfigFactory.load();
        this.system = ActorSystem.create("CatEyeContainer", config);

        Cluster.get(system).registerOnMemberUp(this::init);
    }

    private void init() {

    }

}
