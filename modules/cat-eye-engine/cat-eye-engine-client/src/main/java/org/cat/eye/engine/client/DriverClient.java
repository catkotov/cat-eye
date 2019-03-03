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
import org.cat.eye.engine.common.util.CatEyeActorUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kotov on 22.02.2019.
 */
public class DriverClient {

    private ActorRef clusterClient;

    private String domain;

    public DriverClient(String domain) {

        Config config = ConfigFactory.load();

        this.domain = domain;

        ActorSystem system = ActorSystem.create(domain + "-CatEyeEngineClient", config);

        this.clusterClient = system.actorOf(
                ClusterClient.props(ClusterClientSettings.create(system)
                        .withInitialContacts(initialContacts())),
                domain + "-engine-client"
        );
    }

    public void send(Message.NewComputation computation) {

        clusterClient.tell(
                new ClusterClient.Send("/user/" + this.domain + "-driver-actor", computation, true),
                ActorRef.noSender()
        );
    }

    public void publish(Message.NewComputation computationMsg) {
        clusterClient.tell(
                new ClusterClient.Publish(
                        CatEyeActorUtil.getTopicName(domain, MsgTopic.NEW_COMPUTATION),
                        computationMsg
                ),
                ActorRef.noSender()
        );
    }

    private Set<ActorPath> initialContacts() {

        return new HashSet<>(Collections.singletonList(
                ActorPaths.fromString("akka.tcp://TEST_DOMAIN@127.0.0.1:2551/system/receptionist"))
        );
    }
}
