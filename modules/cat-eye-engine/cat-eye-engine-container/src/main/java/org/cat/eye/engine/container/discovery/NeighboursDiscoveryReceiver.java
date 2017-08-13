package org.cat.eye.engine.container.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by Kotov on 12.08.2017.
 */
public class NeighboursDiscoveryReceiver implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(NeighboursDiscoveryReceiver.class);

    private DatagramReceiver datagramReceiver;

    private Charset charset = Charset.defaultCharset();
    private CharsetDecoder decoder = charset.newDecoder();


    @Override
    public void run() {

        while (true) {
            try {
                ByteBuffer buffer = this.datagramReceiver.getDatagram();
                CharBuffer charBuffer = decoder.decode(buffer);
                LOGGER.info("run - message was received: " + charBuffer.toString());
                charBuffer.clear();
            } catch (Exception e) {
                LOGGER.error("run - error during receive and decode datagram: " + e.getMessage(), e);
            }
        }
    }

    public void setDatagramReceiver(DatagramReceiver datagramReceiver) {
        this.datagramReceiver = datagramReceiver;
    }
}
