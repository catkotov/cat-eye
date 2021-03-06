package org.cat.eye.engine.common.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kotov on 15.09.2017.
 */
public interface Computation extends Serializable {

    UUID getId();

    UUID getParentId();

    String getComputerName();

    String getDomain();

    Object getComputer();

    int getNextStep();

    void setNextStep(int nextStep);

    ComputationState getState();

    void setState(ComputationState state);

    void setChildrenIDs(List<UUID> childrenIDs);

    void addCompletedChildId(UUID childId);

    boolean isChildrenCompleted();

    String toString();
}
