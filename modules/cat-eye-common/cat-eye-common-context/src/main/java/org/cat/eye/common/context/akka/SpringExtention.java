package org.cat.eye.common.context.akka;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.context.ApplicationContext;


public class SpringExtention extends AbstractExtensionId<SpringExtention.AkkaExt> {

    public static SpringExtention SPRING_EXTENTION_PROVIDER = new SpringExtention();

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
