package io.github.md5sha256.messaging.client.network;

import io.github.md5sha256.messaging.network.IMessage;
import io.github.md5sha256.messaging.network.IMessageType;
import org.jetbrains.annotations.NotNull;


public interface IMessageProcessor<T extends IMessageType> {

    @NotNull T messageType();

    void processMessage(@NotNull NetworkClient networkClient, @NotNull IMessage<T> message) throws MessageProcessException;

}
