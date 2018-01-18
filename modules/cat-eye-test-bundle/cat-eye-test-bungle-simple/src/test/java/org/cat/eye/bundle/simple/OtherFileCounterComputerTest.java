package org.cat.eye.bundle.simple;

import org.cat.eye.engine.container.unit.CatEyeContainerUnit;
import org.cat.eye.engine.container.unit.CatEyeContainerUnitFactory;
import org.junit.Test;


/**
 * Created by Kotov on 12.01.2018.
 */
public class OtherFileCounterComputerTest {

    private CatEyeContainerUnit containerUnit = CatEyeContainerUnitFactory.getContaner();

    @Test
    public void fileCounterTest() throws Exception {

        containerUnit.setPathToClasses("E:\\Projects\\cat-eye\\cat-eye\\modules\\cat-eye-test-bundle\\cat-eye-test-bungle-simple\\target\\classes");
        containerUnit.setBundleDomain("TEST_DOMAIN");
        containerUnit.initialize();
    }
}
