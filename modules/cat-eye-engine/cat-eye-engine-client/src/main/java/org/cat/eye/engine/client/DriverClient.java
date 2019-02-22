package org.cat.eye.engine.client;

import akka.actor.ActorPath;
import akka.actor.ActorPaths;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientSettings;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.cat.eye.engine.common.MsgTopic;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.msg.Message;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kotov on 22.02.2019.
 */
public class DriverClient {

    private ActorRef clusterClient;

    public DriverClient() {

        Config config = ConfigFactory.load();

        ActorSystem system = ActorSystem.create("CatEyeEngineClient", config);

        this.clusterClient = system.actorOf(
                ClusterClient.props(ClusterClientSettings.create(system)
                        .withInitialContacts(initialContacts())),
                "engine-client"
        );
    }

    public void send(Message.NewComputation computation) {

        clusterClient.tell(
                new ClusterClient.Publish(MsgTopic.NEW_COMPUTATION.getTopicName(), computation),
                ActorRef.noSender()
        );
    }

    private Set<ActorPath> initialContacts() {

        return new HashSet<>(Collections.singletonList(
                ActorPaths.fromString("akka.tcp://CatEyeContainer@127.0.0.1:2551/system/receptionist"))
        );
    }
}
