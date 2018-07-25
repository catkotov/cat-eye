package org.cat.eye.common.context.akka;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.context.ApplicationContext;


public class AkkaForSpringExtention extends AbstractExtensionId<AkkaForSpringExtention.AkkaExt> {

    public static AkkaForSpringExtention INSTANCE = new AkkaForSpringExtention();

    @Override
    public AkkaExt createExtension(ExtendedActorSystem system) {
        return new AkkaExt();
    }

    public static class AkkaExt implements Extension {

        private volatile ApplicationContext applicationContext;

        public void initialize(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        public Props props(String actorBeanName) {
            return Props.create(SpringActorProducer.class, this.applicationContext, actorBeanName);
        }
    }
}
