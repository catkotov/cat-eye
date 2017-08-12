package org.cat.eye.engine.container.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Date;

/**
 *
 * Created by Kotov at 12.08.2017
 */
public class NeighboursDiscoveryLeadingLight implements Runnable {

    Logger LOGGER = LoggerFactory.getLogger(NeighboursDiscoveryLeadingLight.class);

    private final static int DEFAULT_PORT = 5555;

    private final static String GROUP = "233.252.20.20";

    ByteBuffer buffer;

    @Override
    public void run() {

        try (DatagramChannel datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET)) {

            if (datagramChannel.isOpen()) {

                NetworkInterface networkInterface = NetworkInterface.getByName("wlan0");

                datagramChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
                datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

                datagramChannel.bind(new InetSocketAddress(DEFAULT_PORT));

                while (true) {
                    LOGGER.info("run - Sending signal.");

                    buffer = ByteBuffer.wrap(("Container 1 - " + new Date().toString()).getBytes());

                    datagramChannel.send(buffer, new InetSocketAddress(InetAddress.getByName(GROUP), DEFAULT_PORT));

                    buffer.flip();
                    LOGGER.info("run - Signal was sent.");

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        LOGGER.error("run - error: " + e.getMessage(), e);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("run - error open of datagram channel.", e);
        }
    }
}
