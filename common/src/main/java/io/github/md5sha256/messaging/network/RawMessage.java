package io.github.md5sha256.messaging.network;

import org.jetbrains.annotations.NotNull;

public class RawMessage<T extends IMessageType> implements IMessage<T> {

    private final T messageType;
    private final byte[] data;

    public RawMessage(@NotNull T messageType, byte[] data) {
        this.messageType = messageType;
        this.data = data;
    }

    @Override
    public @NotNull T messageType() {
        return this.messageType;
    }

    @Override
    public byte[] data() {
        return this.data;
    }

}
