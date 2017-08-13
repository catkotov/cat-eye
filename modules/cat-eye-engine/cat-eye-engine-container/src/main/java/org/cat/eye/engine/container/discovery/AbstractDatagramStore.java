package org.cat.eye.engine.container.discovery;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Kotov on 13.08.2017.
 */
public class AbstractDatagramStore {

    int DEFAULT_PORT = 5555;

    String GROUP = "233.252.20.20";

    final int MAX_PACKET_SIZE = 65507;

    private LinkedBlockingQueue<ByteBuffer> datagramStore;

    public AbstractDatagramStore(int datagramStoreCapacity) {
        this.datagramStore = new LinkedBlockingQueue<>(datagramStoreCapacity);
    }

    public boolean addDatagram(ByteBuffer buffer) {
        return this.datagramStore.offer(buffer);
    }

    public ByteBuffer getDatagram() throws InterruptedException {
        return this.datagramStore.take();
    }
}
