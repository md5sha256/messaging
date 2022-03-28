package io.github.md5sha256.messaging.client.network;

import io.github.md5sha256.messaging.network.IMessageType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MessageHandler {

    private final Map<IMessageType, IMessageProcessor<?>> processorMap = new HashMap<>();
    private final Map<Integer, IMessageType> messageTypeMap = new HashMap<>();

    public void registerMessageType(@NotNull IMessageType messageType) {
        this.messageTypeMap.put(Arrays.hashCode(messageType.key().networkId()), messageType);
    }

    public @NotNull Optional<? extends IMessageType> findMessageType(byte[] raw) {
        return Optional.ofNullable(this.messageTypeMap.get(Arrays.hashCode(raw)));
    }

    public <T extends IMessageType> void registerProcessor(@NotNull T type, @NotNull IMessageProcessor<T> processor) {
        this.processorMap.put(type, processor);
    }

    @SuppressWarnings("unchecked")
    public <T extends IMessageType> @NotNull Optional<@NotNull IMessageProcessor<T>> findProcessor(@NotNull T messageType) {
        return Optional.ofNullable((IMessageProcessor<T>) this.processorMap.get((messageType)));
    }


    public @NotNull Optional<IMessageProcessor<?>> findProcessor(byte[] messageType) {
        return findMessageType(messageType).flatMap(this::findProcessor);
    }

}
