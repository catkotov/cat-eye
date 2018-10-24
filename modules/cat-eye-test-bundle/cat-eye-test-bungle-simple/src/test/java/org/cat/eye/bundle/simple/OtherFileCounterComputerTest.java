package org.cat.eye.bundle.simple;

import akka.actor.ActorRef;
import akka.pattern.PatternsCS;
import org.cat.eye.engine.common.crusher.computation.ComputationFactory;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.service.impl.ComputationsQueueActor;
import org.cat.eye.engine.container.unit.CatEyeContainerUnit;
import org.cat.eye.engine.container.unit.CatEyeContainerUnitConfig;
import org.cat.eye.test.bundle.simple.StartFileCounterComputer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.UUID;


/**
 * Created by Kotov on 12.01.2018.
 */
@ContextConfiguration(classes = CatEyeContainerUnitConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class OtherFileCounterComputerTest {

    @Autowired
    private CatEyeContainerUnit containerUnit;

    @Test
    public void fileCounterTest() throws Exception {

        containerUnit.setPathToClasses("D:\\Sand-box\\cat-eye\\modules\\cat-eye-test-bundle\\cat-eye-test-bungle-simple\\target\\classes");
        containerUnit.setBundleDomain("TEST_DOMAIN");
        ActorRef contextService = containerUnit.getComputationQueue();

        Computation computation =
                ComputationFactory.create(new StartFileCounterComputer("C:\\Java"), UUID.randomUUID(), "TEST_DOMAIN");

        ComputationsQueueActor.ReadyComputation readyComputation = new ComputationsQueueActor.ReadyComputation(computation);

        PatternsCS.ask(contextService, readyComputation, 1000).toCompletableFuture().join();

        containerUnit.initialize();

    }
}
