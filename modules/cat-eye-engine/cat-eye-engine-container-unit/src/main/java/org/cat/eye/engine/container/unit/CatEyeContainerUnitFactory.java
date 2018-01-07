package org.cat.eye.engine.container.unit;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Kotov on 07.01.2018.
 */
public class CatEyeContainerUnitFactory {

    public static CatEyeContainerUnit getContaner() {

        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(CatEyeContainerUnitConfig.class);

        return ac.getBean(CatEyeContainerUnit.class);
    }
}
