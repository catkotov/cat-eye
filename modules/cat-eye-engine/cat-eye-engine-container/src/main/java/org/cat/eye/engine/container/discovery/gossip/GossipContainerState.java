package org.cat.eye.engine.container.discovery.gossip;

import org.cat.eye.engine.common.CatEyeContainerRole;
import org.cat.eye.engine.common.CatEyeContainerState;

/**
 * Created by Kotov on 25.08.2017.
 */
public class GossipContainerState {

    private String containerName;

    private Heartbeat containerHeartbeat;

    private CatEyeContainerRole containerRole;

    private CatEyeContainerState containerState;

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public synchronized Heartbeat getContainerHeartbeat() {
        return containerHeartbeat;
    }

    public synchronized void setContainerHeartbeat(Heartbeat containerHeartbeat) {
        this.containerHeartbeat = containerHeartbeat;
    }

    public CatEyeContainerRole getContainerRole() {
        return containerRole;
    }

    public void setContainerRole(CatEyeContainerRole containerRole) {
        this.containerRole = containerRole;
    }

    public CatEyeContainerState getContainerState() {
        return containerState;
    }

    public void setContainerState(CatEyeContainerState containerState) {
        this.containerState = containerState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GossipContainerState)) return false;

        GossipContainerState that = (GossipContainerState) o;

        return containerName.equals(that.containerName)
                && containerHeartbeat.equals(that.containerHeartbeat)
                && containerRole == that.containerRole
                && containerState == that.containerState;
    }

    @Override
    public int hashCode() {
        int result = containerName.hashCode();
        result = 31 * result + containerHeartbeat.hashCode();
        result = 31 * result + containerRole.hashCode();
        result = 31 * result + containerState.hashCode();
        return result;
    }
}
