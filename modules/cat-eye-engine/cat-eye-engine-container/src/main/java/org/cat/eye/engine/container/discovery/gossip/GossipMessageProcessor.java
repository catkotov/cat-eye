package org.cat.eye.engine.container.discovery.gossip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Kotov on 28.08.2017.
 */
public class GossipMessageProcessor implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(GossipMessageProcessor.class);

    private GossipContainerState containerState;

    private GossipNeighboursState neighboursState;

    private LinkedBlockingQueue<GossipMsg> gossipMsgStore = new LinkedBlockingQueue<>();

    public GossipMessageProcessor(GossipContainerState containerState,
                                  GossipNeighboursState neighboursState) {
        this.containerState = containerState;
        this.neighboursState = neighboursState;
    }

    @Override
    public void run() {
        while (true) {
            try {
                GossipContainerState newRemoteContainerState = getMessage().getContainerState();

                if (!newRemoteContainerState.getContainerName().equals(containerState.getContainerName())) {
                    LOGGER.info("run - process message from " + newRemoteContainerState.getContainerName()
                        + " generation: " + newRemoteContainerState.getContainerHeartbeat().getGeneration()
                        + " , version: " + newRemoteContainerState.getContainerHeartbeat().getVersion());
                    GossipContainerState oldRemoteContainerState =
                            neighboursState.getNeighboursState().get(newRemoteContainerState.getContainerName());
                    if (oldRemoteContainerState == null) {
                        neighboursState.getNeighboursState()
                                .put(newRemoteContainerState.getContainerName(), newRemoteContainerState);
                    } else {
                        neighboursState.getNeighboursState().put(newRemoteContainerState.getContainerName(),
                                updateContainerState(newRemoteContainerState, oldRemoteContainerState));
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error("run - error of gossip message processing.", e);
            }
        }
    }

    private GossipContainerState updateContainerState(GossipContainerState newContainerState,
                                                      GossipContainerState oldContainerState) {
        if (newContainerState.getContainerHeartbeat().getGeneration()
                == oldContainerState.getContainerHeartbeat().getGeneration()) {
            if (newContainerState.getContainerHeartbeat().getVersion()
                    > oldContainerState.getContainerHeartbeat().getVersion()) {
                return newContainerState;
            } else {
                return oldContainerState;
            }
        } else {
            return newContainerState;
        }
    }

    public void addMessage(GossipMsg msg) {
        this.gossipMsgStore.offer(msg);
    }

    private GossipMsg getMessage() throws InterruptedException {
        return this.gossipMsgStore.take();
    }
}
