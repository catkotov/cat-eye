package org.cat.eye.engine.container.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;

/**
 * Created by Kotov on 13.08.2017.
 */
public class DatagramReceiver extends AbstractDatagramStore implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(DatagramReceiver.class);

    public DatagramReceiver(int datagramStoreCapacity) {
        super(datagramStoreCapacity);
    }

    @Override
    public void run() {
        try (DatagramChannel datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET)) {

            InetAddress group = InetAddress.getByName(GROUP);

            if (group.isMulticastAddress()) {

                if (datagramChannel.isOpen()) {
                    NetworkInterface networkInterface = NetworkInterface.getByName("wlan0");

                    datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

                    datagramChannel.bind(new InetSocketAddress(DEFAULT_PORT));

                    MembershipKey key = datagramChannel.join(group, networkInterface);

                    while (true) {
                        if (key.isValid()) {
                            ByteBuffer buffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);
                            datagramChannel.receive(buffer);
                            buffer.flip();
                            addDatagram(buffer);
                            LOGGER.info("run - Datagram was received.");
                        } else {
                            LOGGER.info("run - membership key isn't valid.");
                            break;
                        }
                    }
                } else {
                    throw new RuntimeException("Datagram channel isn't open.");
                }
            }
        } catch (IOException e) {
            LOGGER.error("run - error open of datagram channel.", e);
        }
    }
}
