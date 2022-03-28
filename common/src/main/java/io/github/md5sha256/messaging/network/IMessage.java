package io.github.md5sha256.messaging.network;

import org.jetbrains.annotations.NotNull;

public interface IMessage<T extends IMessageType> {

    @NotNull T messageType();

    byte[] data();

}
