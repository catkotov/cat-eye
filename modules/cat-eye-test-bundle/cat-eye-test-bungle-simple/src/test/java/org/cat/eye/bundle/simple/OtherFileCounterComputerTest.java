package org.cat.eye.bundle.simple;

import org.cat.eye.engine.common.crusher.computation.ComputationFactory;
import org.cat.eye.engine.common.service.ComputationContextService;
import org.cat.eye.engine.container.unit.CatEyeContainerUnit;
import org.cat.eye.engine.container.unit.CatEyeContainerUnitFactory;
import org.cat.eye.test.bundle.simple.StartFileCounterComputer;
import org.junit.Test;

import java.util.UUID;


/**
 * Created by Kotov on 12.01.2018.
 */
public class OtherFileCounterComputerTest {

    private CatEyeContainerUnit containerUnit = CatEyeContainerUnitFactory.getContaner();

    @Test
    public void fileCounterTest() throws Exception {

        containerUnit.setPathToClasses("D:\\Sand-box\\cat-eye\\modules\\cat-eye-test-bundle\\cat-eye-test-bungle-simple\\target\\classes");
        containerUnit.setBundleDomain("TEST_DOMAIN");
        ComputationContextService contextService = containerUnit.getComputationContextService();

        contextService.putReadyComputationToQueue(ComputationFactory.create(new StartFileCounterComputer("C:\\Java"), UUID.randomUUID(), "TEST_DOMAIN"));
        containerUnit.initialize();
    }
}
