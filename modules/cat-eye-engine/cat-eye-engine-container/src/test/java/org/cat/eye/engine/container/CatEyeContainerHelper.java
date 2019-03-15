package org.cat.eye.engine.container;

/**
 * Created by Kotov on 21.02.2019.
 */
public class CatEyeContainerHelper {

    public static void main(String[] argv) {

        CatEyeContainer.start("driver");
        CatEyeContainer.start("dispatcher");
//        CatEyeContainer dispatcher2 = new CatEyeContainer("dispatcher");
//        CatEyeContainer dispatcher3 = new CatEyeContainer("dispatcher");
//        CatEyeContainer engine1 = new CatEyeContainer("engine");
//        CatEyeContainer engine2 = new CatEyeContainer("engine");
//        CatEyeContainer engine3 = new CatEyeContainer("engine");
//        CatEyeContainer engine4 = new CatEyeContainer("engine");
//        CatEyeContainer engine5 = new CatEyeContainer("engine");
//        CatEyeContainer engine6 = new CatEyeContainer("engine");
//        CatEyeContainer engine7 = new CatEyeContainer("engine");
//        CatEyeContainer engine8 = new CatEyeContainer("engine");
        CatEyeContainer.start("engine");
    }
}
