package org.cat.eye.engine.container.msg;

import org.cat.eye.engine.common.CatEyeContainerRole;
import org.cat.eye.engine.common.CatEyeContainerState;
import org.cat.eye.engine.container.discovery.gossip.GossipContainerState;
import org.cat.eye.engine.container.discovery.gossip.GossipMsg;
import org.cat.eye.engine.container.discovery.gossip.GossipNeighboursState;
import org.cat.eye.engine.container.discovery.gossip.Heartbeat;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Map;

import static org.junit.Assert.*;

public class CatEyeContainerMessageTest {
    @Test
    public void createAndParseDatagram() throws Exception {

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

        CatEyeContainerMessage message = new CatEyeContainerMessage(GossipMsg.class.getName(), buffer);

        ByteBuffer messageBuffer = CatEyeContainerMessage.createDatagram(message);

        CatEyeContainerMessage newMessage = CatEyeContainerMessage.parseDatagram(messageBuffer);

        assertNotNull(newMessage);
        assertTrue(newMessage.getMessageType().equals(GossipMsg.class.getName()));
        newMessage.getMessage().flip();
        assertEquals(0, buffer.compareTo(newMessage.getMessage()));
    }

}