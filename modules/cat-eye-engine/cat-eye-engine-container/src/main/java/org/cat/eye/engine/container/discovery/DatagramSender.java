package org.cat.eye.engine.container.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;

/**
 * Created by Kotov on 13.08.2017.
 */
public class DatagramSender extends AbstractDatagramStore implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(DatagramSender.class);

    private String networkInterfaceName;

    public DatagramSender(int datagramStoreCapacity) {
        super(datagramStoreCapacity);
    }

    @Override
    public void run() {
        try (DatagramChannel datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET)) {

            if (datagramChannel.isOpen()) {

                NetworkInterface networkInterface = NetworkInterface.getByName(networkInterfaceName);

                datagramChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
                datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

                datagramChannel.bind(new InetSocketAddress(DEFAULT_PORT));

                while (true) {
                    LOGGER.info("run - Sending datagram.");
                    datagramChannel.send(
                            getDatagram(),
                            new InetSocketAddress(InetAddress.getByName(GROUP), DEFAULT_PORT)
                    );
                    LOGGER.info("run - Datagram was sent.");
                }
            } else {
                throw new RuntimeException("Datagram channel isn't open.");
            }
        } catch (InterruptedException e) {
            LOGGER.error("run - error during getting datagram from the store.", e);
        } catch (IOException e) {
            LOGGER.error("run - error open of datagram channel.", e);
        }
    }

    public void setNetworkInterfaceName(String networkInterfaceName) {
        this.networkInterfaceName = networkInterfaceName;
    }
}
