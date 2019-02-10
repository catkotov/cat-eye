package org.cat.eye.bundle.simple;

import akka.actor.ActorRef;
import org.cat.eye.engine.common.crusher.computation.ComputationFactory;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.container.unit.AkkaCatEyeContainerUnit;
import org.cat.eye.engine.common.actors.ComputationDriverUnit;
import org.cat.eye.test.bundle.simple.StartFileCounterComputer;
import org.junit.Test;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Kotov on 12.01.2018.
 */
public class FileCounterComputerTest {

    private static final String PATH_TO_CLASS =
            "E:\\Projects\\cat-eye\\cat-eye\\modules\\cat-eye-test-bundle\\cat-eye-test-bungle-simple\\target\\classes";

    private static final String DOMAIN = "TEST_DOMAIN";

    @Test
    public void fileCounterAkkaTest() throws Exception {
        AkkaCatEyeContainerUnit containerUnit = new AkkaCatEyeContainerUnit(PATH_TO_CLASS, DOMAIN);
        CountDownLatch latch = containerUnit.getLatch();
        ActorRef driver = containerUnit.initialize();

        Computation computation =
                ComputationFactory.create(new StartFileCounterComputer("C:\\Java"), null, DOMAIN);

        ComputationDriverUnit.NewComputation newComputation = new ComputationDriverUnit.NewComputation(computation);

        driver.tell(newComputation, ActorRef.noSender());

        latch.await();
    }
}
