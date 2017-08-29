package org.cat.eye.engine.container.discovery;

import org.cat.eye.engine.container.datagram.DatagramReceiver;
import org.cat.eye.engine.container.discovery.gossip.GossipMessageProcessor;
import org.cat.eye.engine.container.discovery.gossip.GossipMsg;
import org.cat.eye.engine.container.msg.CatEyeContainerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

/**
 * Created by Kotov on 12.08.2017.
 */
public class NeighboursDiscoveryReceiver implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(NeighboursDiscoveryReceiver.class);

    private DatagramReceiver datagramReceiver;

    private GossipMessageProcessor messageProcessor;

    @Override
    public void run() {

        while (true) {
            try {
                ByteBuffer buffer = this.datagramReceiver.getDatagram();
                CatEyeContainerMessage message = CatEyeContainerMessage.parseDatagram(buffer);
                processMessage(message);
            } catch (Exception e) {
                LOGGER.error("run - error during receive and decode datagram: " + e.getMessage(), e);
            }
        }
    }

    private void processMessage(CatEyeContainerMessage message) throws CharacterCodingException {
        if (GossipMsg.class.getName().equals(message.getMessageType())) {

            GossipMsg gossipMsg = GossipMsg.unmarshal(message.getMessage());
            messageProcessor.addMessage(gossipMsg);
        }
    }

    public void setDatagramReceiver(DatagramReceiver datagramReceiver) {
        this.datagramReceiver = datagramReceiver;
    }

    public void setMessageProcessor(GossipMessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }
}
