package org.cat.eye.engine.container;

/**
 * Created by Kotov on 21.02.2019.
 */
public class CatEyeContainerHelper {

    public static void main(String[] argv) {

        CatEyeContainer driver = new CatEyeContainer("driver");
        CatEyeContainer dispatcher = new CatEyeContainer("dispatcher");
        CatEyeContainer engine = new CatEyeContainer("engine");
    }
}
