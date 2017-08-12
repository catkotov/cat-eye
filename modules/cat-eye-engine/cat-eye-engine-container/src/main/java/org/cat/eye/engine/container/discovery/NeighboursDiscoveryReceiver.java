package org.cat.eye.engine.container.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by Kotov on 12.08.2017.
 */
public class NeighboursDiscoveryReceiver implements Runnable {

    Logger LOGGER = LoggerFactory.getLogger(NeighboursDiscoveryReceiver.class);

    private final static int DEFAULT_PORT = 5555;
    private final static String GROUP = "233.252.20.20";
    private final int MAX_PACKET_SIZE = 65507;

    CharBuffer charBuffer = null;
    Charset charset = Charset.defaultCharset();
    CharsetDecoder decoder = charset.newDecoder();
    ByteBuffer buffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);

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
                            datagramChannel.receive(buffer);
                            buffer.flip();
                            charBuffer = decoder.decode(buffer);
                            LOGGER.info("run - message was received: " + charBuffer.toString());
                            buffer.clear();
                        } else {
                            LOGGER.info("run - membership key isn't valid.");
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("run - error open of datagram channel.", e);
        }
    }
}
