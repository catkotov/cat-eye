package org.cat.eye.engine.container;

/**
 * Created by Kotov on 21.02.2019.
 */
public class CatEyeContainerHelper {

    public static void main(String[] argv) {

        CatEyeContainer driver = new CatEyeContainer("driver", 2551);
        CatEyeContainer dispatcher = new CatEyeContainer("dispatcher", 2552);
        CatEyeContainer engine = new CatEyeContainer("engine", 2553);
    }
}
