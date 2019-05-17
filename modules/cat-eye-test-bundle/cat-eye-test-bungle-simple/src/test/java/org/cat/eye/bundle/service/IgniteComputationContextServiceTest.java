package org.cat.eye.bundle.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.cat.eye.engine.common.crusher.computation.ComputationFactory;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.model.ComputationState;
import org.cat.eye.test.bundle.service.IgniteComputationContextService;
import org.junit.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class IgniteComputationContextServiceTest {

    private  static IgniteComputationContextService computationContextService;

    private static Ignite ignite;

    @BeforeClass
    public static void init() {
        ignite = Ignition.start("default-config.xml");

        computationContextService = new IgniteComputationContextService();
    }

    @AfterClass
    public static void close() {
        computationContextService.close();

        if (ignite != null) {
            ignite.close();
        }
    }

    @Test
    public void storeComputation() {

        Computation computation = ComputationFactory.create(new MockEmptyComputer(), null, "DOMAIN");

        computationContextService.storeComputation(computation);
        Computation result = computationContextService.getComputation(computation.getId());

        assertEquals(computation, result);
    }

    @Test
    public void tryToRunComputation() {

        Computation computation = ComputationFactory.create(new MockEmptyComputer(), null, "DOMAIN");

        boolean result = computationContextService.tryToRunComputation(computation);

        assertTrue(result);

        result = computationContextService.tryToRunComputation(computation);

        assertFalse(result);

        Computation running = computationContextService.getRunningComputation(computation.getId());

        assertEquals(computation, running);
    }

    @Test
    public void addCompletedChildIdAndRefresh() {

        Computation parentComputation = ComputationFactory.create(new MockEmptyComputer(), null, "DOMAIN");

        Computation child_1 = ComputationFactory.create(new MockEmptyComputer(), parentComputation.getId(), "DOMAIN");
        Computation child_2 = ComputationFactory.create(new MockEmptyComputer(), parentComputation.getId(), "DOMAIN");

        List<Computation> childrenComputations = new ArrayList<>();
        childrenComputations.add(child_1);
        childrenComputations.add(child_2);

        computationContextService.storeComputation(parentComputation);

        computationContextService.registerChildrenComputations(parentComputation, childrenComputations);

        child_1.setState(ComputationState.COMPLETED);
        parentComputation = computationContextService.addCompletedChildIdAndRefresh(parentComputation.getId(), child_1);

        assertFalse(parentComputation.isChildrenCompleted());

        child_2.setState(ComputationState.COMPLETED);
        parentComputation = computationContextService.addCompletedChildIdAndRefresh(parentComputation.getId(), child_2);

        assertTrue(parentComputation.isChildrenCompleted());
    }

    @Test
    public void fromRunningToWaiting() {

        Computation computation = ComputationFactory.create(new MockEmptyComputer(), null, "DOMAIN");

        computationContextService.tryToRunComputation(computation);

        Computation test = computationContextService.getComputation(computation.getId());

        assertEquals(computation, test);
        assertEquals(ComputationState.RUNNING, test.getState());

        test = computationContextService.getRunningComputation(computation.getId());

        assertEquals(computation, test);
        assertEquals(ComputationState.RUNNING, test.getState());

        computation = computationContextService.getRunningComputation(computation.getId());

        computationContextService.fromRunningToWaiting(computation);

        test = computationContextService.getComputation(computation.getId());
        assertEquals(ComputationState.WAITING, test.getState());

        test = computationContextService.getRunningComputation(computation.getId());

        assertNull(test);
    }

    @Test
    public void fromRunningToReady() {

        Computation computation = ComputationFactory.create(new MockEmptyComputer(), null, "DOMAIN");

        computationContextService.tryToRunComputation(computation);

        Computation test = computationContextService.getComputation(computation.getId());

        assertEquals(computation, test);
        assertEquals(ComputationState.RUNNING, test.getState());

        test = computationContextService.getRunningComputation(computation.getId());

        assertEquals(computation, test);
        assertEquals(ComputationState.RUNNING, test.getState());

        computation = computationContextService.getRunningComputation(computation.getId());

        computationContextService.fromRunningToReady(computation);

        test = computationContextService.getComputation(computation.getId());
        assertEquals(ComputationState.READY, test.getState());

        test = computationContextService.getRunningComputation(computation.getId());

        assertNull(test);
    }

    @Test
    public void fromRunningToCompleted() {

        Computation computation = ComputationFactory.create(new MockEmptyComputer(), null, "DOMAIN");

        computationContextService.tryToRunComputation(computation);

        Computation test = computationContextService.getComputation(computation.getId());

        assertEquals(computation, test);
        assertEquals(ComputationState.RUNNING, test.getState());

        test = computationContextService.getRunningComputation(computation.getId());

        assertEquals(computation, test);
        assertEquals(ComputationState.RUNNING, test.getState());

        computation = computationContextService.getRunningComputation(computation.getId());

        computationContextService.fromRunningToCompleted(computation);

        test = computationContextService.getComputation(computation.getId());
        assertEquals(ComputationState.COMPLETED, test.getState());

        test = computationContextService.getRunningComputation(computation.getId());

        assertNull(test);
    }

    @Test
    public void fromWaitingToReady() {

        Computation computation = ComputationFactory.create(new MockEmptyComputer(), null, "DOMAIN");
        computation.setState(ComputationState.WAITING);
        computationContextService.storeComputation(computation);

        computationContextService.fromWaitingToReady(computation);

        Computation test = computationContextService.getComputation(computation.getId());

        assertEquals(ComputationState.READY, test.getState());
    }

}