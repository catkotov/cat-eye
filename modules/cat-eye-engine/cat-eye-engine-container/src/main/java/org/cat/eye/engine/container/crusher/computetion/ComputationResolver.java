package org.cat.eye.engine.container.crusher.computetion;

import org.cat.eye.engine.model.Computation;
import org.cat.eye.engine.model.annotation.Compute;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kotov on 15.09.2017.
 */
public class ComputationResolver {

    Map<Class<?>, List<Method>> computerMap = new ConcurrentHashMap<>();


    public List<Computation> resolve(Computation computation) {

        return null;
    }

    public void addComputer(Object computer) {

        Class<?> computerClass = computer.getClass();

        Method[] methods = computerClass.getMethods();

        for (Method method : methods) {
            List<Method> methodLst = new ArrayList<>();
            if (method.isAnnotationPresent(Compute.class)) {
                methodLst.add(method);
            }
            if (!methodLst.isEmpty()) {
                methodLst.sort((m1, m2) -> {
                    Compute a1 = m1.getAnnotation(Compute.class);
                    int order1 = a1.step();
                    Compute a2 = m2.getAnnotation(Compute.class);
                    int order2 = a2.step();
                    return Integer.compare(order1, order2);
                });

                computerMap.put(computerClass, methodLst);
            }
        }
    }

}
