package org.cat.eye.engine.service.ignite.cache;

import org.cat.eye.engine.common.crusher.computation.ComputationFactory;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.model.ComputationState;
import org.cat.eye.test.bundle.simple.StartFileCounterComputer;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class IgniteComputationContextServiceTest {

    private  static IgniteComputationContextService computationContextService;

    @BeforeClass
    public static void init() {
        computationContextService = new IgniteComputationContextService("127.0.0.1:47500..47509");
    }

    @AfterClass
    public static void close() {
        computationContextService.close();
    }

    @Test
    public void storeComputation() {

        Computation computation = ComputationFactory.create(new StartFileCounterComputer("C:\\Java"), null, "DOMAIN");

        computationContextService.storeComputation(computation);
        Computation result = computationContextService.getComputation(computation.getId());

        assertEquals(computation, result);
    }

    @Test
    public void tryToRunComputation() {

        Computation computation = ComputationFactory.create(new StartFileCounterComputer("C:\\Java"), null, "DOMAIN");

        boolean result = computationContextService.tryToRunComputation(computation);

        assertTrue(result);

        result = computationContextService.tryToRunComputation(computation);

        assertFalse(result);
    }

    @Test
    public void addCompletedChildIdAndRefresh() {

        Computation parentComputation = ComputationFactory.create(new StartFileCounterComputer("C:\\Java"), null, "DOMAIN");

        Computation child_1 = ComputationFactory.create(new StartFileCounterComputer("Child_1"), parentComputation.getId(), "DOMAIN");
        Computation child_2 = ComputationFactory.create(new StartFileCounterComputer("Child_2"), parentComputation.getId(), "DOMAIN");

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
}