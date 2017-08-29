package org.cat.eye.engine.container.discovery;

import org.cat.eye.engine.container.datagram.DatagramSender;
import org.cat.eye.engine.container.discovery.gossip.GossipContainerState;
import org.cat.eye.engine.container.discovery.gossip.GossipMsg;
import org.cat.eye.engine.container.discovery.gossip.GossipNeighboursState;
import org.cat.eye.engine.container.discovery.gossip.Heartbeat;
import org.cat.eye.engine.container.msg.CatEyeContainerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;

/**
 *
 * Created by Kotov on 12.08.2017.
 */
public class NeighboursDiscoveryLeadingLight implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(NeighboursDiscoveryLeadingLight.class);

    private DatagramSender datagramSender;

    private GossipContainerState containerState;

    private GossipNeighboursState neighboursState;

    @Override
    public void run() {
        while (true) {
            LOGGER.info("run - Sending signal.");
            containerState.setContainerHeartbeat(new Heartbeat(containerState.getContainerHeartbeat()));
            datagramSender.addDatagram(createMessage());
            LOGGER.info("run - Signal was sent.");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LOGGER.error("run - error: " + e.getMessage(), e);
            }
        }
    }

    private ByteBuffer createMessage() {

        GossipMsg gossipMsg = new GossipMsg(containerState, neighboursState);
        ByteBuffer gossipMsgBuffer = GossipMsg.marshal(gossipMsg);
        CatEyeContainerMessage message = new CatEyeContainerMessage(GossipMsg.class.getName(), gossipMsgBuffer);

        return CatEyeContainerMessage.createDatagram(message);
    }

    public void setDatagramSender(DatagramSender datagramSender) {
        this.datagramSender = datagramSender;
    }

    public void setContainerState(GossipContainerState containerState) {
        this.containerState = containerState;
    }

    public void setNeighboursState(GossipNeighboursState neighboursState) {
        this.neighboursState = neighboursState;
    }
}
