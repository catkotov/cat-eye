package org.cat.eye.engine.container.discovery.gossip;

import org.cat.eye.engine.common.CatEyeContainerRole;
import org.cat.eye.engine.common.CatEyeContainerState;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Map;

import static org.junit.Assert.*;

public class GossipMsgImplTest {
    @Test
    public void marshalUnmarshalTest() throws Exception {

        GossipContainerState containerState = new GossipContainerState();
        containerState.setContainerName("container1");
        containerState.setContainerRole(CatEyeContainerRole.MASTER);
        containerState.setContainerState(CatEyeContainerState.RUNNING);

        Heartbeat heartbeat = new Heartbeat(1L, 2345L);
        containerState.setContainerHeartbeat(heartbeat);

        GossipNeighboursState neighboursState = new GossipNeighboursState();

        Map<String, GossipContainerState> neighbours = neighboursState.getNeighboursState();
        GossipContainerState neighbourState = new GossipContainerState();
        neighbourState.setContainerName("container2");
        neighbourState.setContainerHeartbeat(new Heartbeat(23L, 5432L));
        neighbourState.setContainerState(CatEyeContainerState.PREPARE_TO_SHUTDOWN);
        neighbourState.setContainerRole(CatEyeContainerRole.UNDEFINED);
        neighbours.put(neighbourState.getContainerName(), neighbourState);

        GossipMsg msg = new GossipMsg(containerState, neighboursState);

        ByteBuffer buffer = GossipMsg.marshal(msg);

        GossipMsg newMsg = GossipMsg.unmarshal(buffer);

        assertNotNull(newMsg);
        assertEquals(msg, newMsg);
    }

}