package org.cat.eye.engine.container.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 *
 * Created by Kotov at 12.08.2017
 */
public class NeighboursDiscoveryLeadingLight implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(NeighboursDiscoveryLeadingLight.class);

    private DatagramSender datagramSender;

    private ByteBuffer buffer;

    @Override
    public void run() {

        while (true) {
            LOGGER.info("run - Sending signal.");

            buffer = ByteBuffer.wrap(("Container 1 - " + new Date().toString()).getBytes());

            datagramSender.addDatagram(buffer);

            LOGGER.info("run - Signal was sent.");

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LOGGER.error("run - error: " + e.getMessage(), e);
            }
        }
    }

    public void setDatagramSender(DatagramSender datagramSender) {
        this.datagramSender = datagramSender;
    }
}
