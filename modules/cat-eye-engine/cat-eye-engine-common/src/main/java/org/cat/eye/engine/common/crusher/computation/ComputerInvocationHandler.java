package org.cat.eye.engine.common.crusher.computation;

import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.model.ComputationState;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class ComputerInvocationHandler implements InvocationHandler, Serializable {

    private Object computer;

    private UUID id;

    private String computerName;

    private String domain;

    private int nextStep = 1;

    private ComputationState state;

    private UUID parentId;

    private Set<UUID> childrenIDs = new CopyOnWriteArraySet<>();

    private Set<UUID> completedChildrenIDs = new CopyOnWriteArraySet<>();

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
                break;
            case "getState":
                return state;
            case "setState":
                state = (ComputationState) args[0];
                break;
            case "getParentId":
                return parentId;
            case "setChildrenIDs":
                childrenIDs.addAll((List<UUID>) args[0]);
                break;
            case "addCompletedChildId":
                completedChildrenIDs.add((UUID) args[0]);
                break;
            case "isChildrenCompleted":
                return childrenIDs.size() == completedChildrenIDs.size();

            case "toString":
                return computer.toString();

            case "hashCode":
                return Objects.hash(id);

            case "equals": {
                return args[0] instanceof Computation && Objects.equals(id, ((Computation) args[0]).getId());
            }

            default: return null;
        }

        return null;
    }

}
