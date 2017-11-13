package org.cat.eye.engine.container.model;

import java.util.UUID;

/**
 * Created by Kotov on 15.09.2017.
 */
public interface Computation {

    UUID getId();

    UUID getParentId();

    String getComputerName();

    String getDomain();

    Object getComputer();

    int getNextStep();

    void setNextStep(int nextStep);

    ComputationState getState();

    void setState(ComputationState state);

    String toString();
}
