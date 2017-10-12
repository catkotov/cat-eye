package org.cat.eye.engine.container.msg;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by Kotov on 27.08.2017.
 */
public class CatEyeContainerMessage {

    private String messageType;

    private ByteBuffer message;

    private static Charset charset = Charset.defaultCharset();

    public CatEyeContainerMessage(String messageType, ByteBuffer message) {
        this.messageType = messageType;
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public ByteBuffer getMessage() {
        return message;
    }

    public static ByteBuffer createDatagram(CatEyeContainerMessage msg) {
        int headerLength = msg.getMessageType().getBytes().length;
        int messageLength = msg.getMessage().array().length;
        ByteBuffer buffer = ByteBuffer.allocate(4 + headerLength + messageLength);
        buffer.putInt(headerLength).put(msg.getMessageType().getBytes()).put(msg.getMessage());

        buffer.flip();

        return buffer;
    }

    public static CatEyeContainerMessage parseDatagram(ByteBuffer buffer) throws CharacterCodingException {

        int headerLength = buffer.getInt();
        int currentPosition = buffer.position();

        buffer.mark().position(currentPosition + headerLength);
        ByteBuffer messageBuffer = buffer.slice();

        buffer.reset().limit(currentPosition + headerLength);
        ByteBuffer typeMsgBuffer = buffer.slice();
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer charBuffer = decoder.decode(typeMsgBuffer);

        return new CatEyeContainerMessage(charBuffer.toString(), messageBuffer);
    }
}
