package org.cat.eye.engine.container.crusher.computetion;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class ComputerInvocationHandler implements InvocationHandler {

    private Object computer;

    private UUID id;

    private String computerName;

    private String domain;

    public ComputerInvocationHandler(UUID id, String computerName, String domain, Object computer) {
        this.id = id;
        this.computerName = computerName;
        this.domain = domain;
        this.computer = computer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        switch (method.getName()) {
            case "getId":
                return id;
            case "getComputerName":
                return computerName;
            case "getDomain":
                return domain;
            case "getComputer":
                return computer;

            default: return null;
        }
    }
}
