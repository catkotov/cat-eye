package org.cat.eye.bundle.simple;

import akka.actor.ActorRef;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.cat.eye.engine.common.crusher.computation.ComputationFactory;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.msg.Message;
import org.cat.eye.engine.container.unit.CatEyeContainerUnit;
import org.cat.eye.test.bundle.simple.StartFileCounterComputer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Kotov on 12.01.2018.
 */
public class FileCounterComputerTest {

    private static final String PATH_TO_CLASS =
            "E:\\Projects\\cat-eye\\cat-eye\\modules\\cat-eye-test-bundle\\cat-eye-test-bungle-simple\\target\\classes";

    private static final String DOMAIN = "TEST_DOMAIN";

    private static Ignite ignite;

    @BeforeClass
    public static void init() {
        ignite = Ignition.start("default-config.xml");
    }

    @AfterClass
    public static void close() {
        if (ignite != null) {
            ignite.close();
        }
    }

    @Test
    public void fileCounterAkkaTest() throws Exception {
        CatEyeContainerUnit containerUnit = new CatEyeContainerUnit(PATH_TO_CLASS, DOMAIN);
        CountDownLatch latch = containerUnit.getLatch();
        ActorRef driver = containerUnit.initialize();

        Computation computation =
                ComputationFactory.create(new StartFileCounterComputer("C:\\Java"), null, DOMAIN);

        Message.NewComputation newComputation = new Message.NewComputation(computation);

        driver.tell(newComputation, ActorRef.noSender());

        latch.await();
    }
}
