package org.cat.eye.engine.container.crusher.computation;

import org.cat.eye.engine.container.model.ComputationState;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class ComputerInvocationHandler implements InvocationHandler {

    private Object computer;

    private UUID id;

    private String computerName;

    private String domain;

    private int nextStep = 1;

    private ComputationState state;

    private UUID parentId;

    public ComputerInvocationHandler(UUID id, UUID parentId, String computerName, String domain, Object computer, ComputationState state) {
        this.id = id;
        this.parentId = parentId;
        this.computerName = computerName;
        this.domain = domain;
        this.computer = computer;
        this.state = state;
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
            case "getNextStep":
                return nextStep;
            case "setNextStep":
                nextStep = (Integer) args[0];
            case "getState":
                return state;
            case "setState":
                state = (ComputationState) args[0];
            case "getParentId":
                return parentId;

            case "toString":
                return computer.toString();

            default: return null;
        }
    }
}
