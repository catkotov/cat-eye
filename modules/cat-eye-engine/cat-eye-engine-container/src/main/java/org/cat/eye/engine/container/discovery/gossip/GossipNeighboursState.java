package org.cat.eye.engine.container.discovery.gossip;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kotov on 27.08.2017.
 */
public class GossipNeighboursState {

    private Map<String, GossipContainerState> neighboursState = new ConcurrentHashMap<>();

    public Map<String, GossipContainerState> getNeighboursState() {
        return this.neighboursState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GossipNeighboursState)) return false;

        GossipNeighboursState that = (GossipNeighboursState) o;

        return neighboursState.equals(that.neighboursState);
    }

    @Override
    public int hashCode() {
        return neighboursState.hashCode();
    }
}
