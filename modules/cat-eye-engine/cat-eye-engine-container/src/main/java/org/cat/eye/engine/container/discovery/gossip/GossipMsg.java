package org.cat.eye.engine.container.discovery.gossip;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by Kotov on 23.08.2017.
 */
public class GossipMsg {

    private static Charset CHARSET = Charset.defaultCharset();

    private GossipContainerState containerState;

    private GossipNeighboursState neighboursState;

    public GossipMsg(GossipContainerState containerState, GossipNeighboursState neighboursState) {
        this.containerState = containerState;
        this.neighboursState = neighboursState;
    }

    public GossipContainerState getContainerState() {
        return containerState;
    }

    public GossipNeighboursState getNeighboursState() {
        return neighboursState;
    }

    public static ByteBuffer marshal(GossipMsg gossipMsg) {

        ObjectMapper mapper = JsonFactory.create();
        String containerStateStr = mapper.writeValueAsString(gossipMsg);

        return ByteBuffer.wrap(containerStateStr.getBytes());
    }

    public static GossipMsg unmarshal(ByteBuffer buffer) throws CharacterCodingException {

        CharsetDecoder decoder = CHARSET.newDecoder();
        CharBuffer charBuffer = decoder.decode(buffer);

        ObjectMapper mapper = JsonFactory.create();
        return mapper.readValue(charBuffer.toString(), GossipMsg.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GossipMsg)) return false;

        GossipMsg msg = (GossipMsg) o;

        if (!containerState.equals(msg.containerState)) return false;
        return neighboursState != null ? neighboursState.equals(msg.neighboursState) : msg.neighboursState == null;
    }

    @Override
    public int hashCode() {
        int result = containerState.hashCode();
        result = 31 * result + (neighboursState != null ? neighboursState.hashCode() : 0);
        return result;
    }
}
