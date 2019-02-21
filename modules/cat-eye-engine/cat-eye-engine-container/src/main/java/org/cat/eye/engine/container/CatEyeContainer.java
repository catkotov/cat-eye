package org.cat.eye.engine.container;

import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.cat.eye.engine.common.ContainerRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Kotov on 18.02.2019.
 */
public class CatEyeContainer {

    private final static Logger LOGGER = LoggerFactory.getLogger(CatEyeContainer.class);

    private Config config;

    private ActorSystem system;

    private ContainerRole role;

    public CatEyeContainer(String roleName, int port) {

        ContainerRole containerRole = ContainerRole.defineRole(roleName);

        if (containerRole != null) {

            this.role = containerRole;

            this.config = ConfigFactory
                    .parseString("akka.cluster.roles = [" + roleName + "]")
                    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port))
                    .withFallback(ConfigFactory.load());

            this.system = ActorSystem.create("CatEyeContainer", config);

            Cluster.get(system).registerOnMemberUp(this::init);

        } else {
            LOGGER.error("CatEyeContainer.CatEyeContainer - you must define role for started container!!!");
        }
    }

    private void init() {
        LOGGER.info("CatEyeContainer.init - start initialization of container " + role.name() + ".");

        LOGGER.info("CatEyeContainer.init - finish initialization of container " + role.name() + ".");
    }

}
