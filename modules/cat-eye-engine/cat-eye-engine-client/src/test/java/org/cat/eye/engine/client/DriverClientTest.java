package org.cat.eye.engine.client;

import org.cat.eye.engine.common.crusher.computation.ComputationFactory;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.msg.Message;
import org.cat.eye.test.bundle.simple.StartFileCounterComputer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class DriverClientTest {

    private static final String PATH_TO_CLASS =
            "E:\\Projects\\cat-eye\\cat-eye\\modules\\cat-eye-test-bundle\\cat-eye-test-bungle-simple\\target\\classes";

    private static final String DOMAIN = "TEST_DOMAIN";

    private DriverClient client;

    @Before
    public void setUp() throws Exception {
        this.client = new DriverClient();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void send() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        Computation computation =
                ComputationFactory.create(new StartFileCounterComputer("C:\\Java"), null, DOMAIN);

        Message.NewComputation newComputation = new Message.NewComputation(computation);

        client.send(newComputation);

        latch.await();
    }
}