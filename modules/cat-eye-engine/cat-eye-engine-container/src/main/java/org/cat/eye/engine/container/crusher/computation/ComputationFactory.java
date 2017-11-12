package org.cat.eye.engine.container.crusher.computation;


import org.cat.eye.engine.container.model.Computation;
import org.cat.eye.engine.container.model.ComputationState;

import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Created by Kotov on 15.09.2017.
 */
public class ComputationFactory {

    public static Computation create(Object computer, String domain) {

        ComputerInvocationHandler handler =
                new ComputerInvocationHandler(UUID.randomUUID(), computer.getClass().getName(), domain, computer, ComputationState.CREATED);

        return (Computation)
                Proxy.newProxyInstance(computer.getClass().getClassLoader(), new Class[] {Computation.class}, handler);
    }




}
