package io.github.md5sha256.messaging.network;

import org.jetbrains.annotations.NotNull;

public class TextMessage implements IMessage<TextMessageType> {

    private final byte[] bytes;

    public TextMessage(@NotNull String text) {
        this.bytes = text.getBytes(Constants.NETWORK_CHARSET);
    }

    public TextMessage(@NotNull IMessage<TextMessageType> message) {
        this.bytes = message.data();
    }

    public @NotNull String getText() {
        if (this.bytes.length == 0) {
            return "";
        }
        return new String(this.bytes, Constants.NETWORK_CHARSET);
    }

    @Override
    public byte[] data() {
        return this.bytes;
    }

    @Override
    public @NotNull TextMessageType messageType() {
        return TextMessageType.INSTANCE;
    }

}
