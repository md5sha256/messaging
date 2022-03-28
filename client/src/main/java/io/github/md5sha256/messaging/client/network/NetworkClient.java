package io.github.md5sha256.messaging.client.network;


import io.github.md5sha256.messaging.network.Constants;
import io.github.md5sha256.messaging.network.IMessage;
import io.github.md5sha256.messaging.network.IMessageType;
import io.github.md5sha256.messaging.network.NetworkConfig;
import io.github.md5sha256.messaging.network.RawMessage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkClient {

    private final NetworkConfig networkConfig;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private MessageHandler messageHandler;

    public NetworkClient(NetworkConfig networkConfig) throws IOException {
        this.networkConfig = networkConfig;
    }

    public @NotNull CompletableFuture<IMessage<?>> readMessage() {
        final CompletableFuture<IMessage<?>> future = new CompletableFuture<>();
        this.executorService.submit(() -> {
            try {
                future.complete(readMessageBlocking());
            } catch (Throwable ex) {
                future.completeExceptionally(ex);
            }
        });
        return future;
    }

    public <T extends IMessageType> @NotNull CompletableFuture<Void> sendMessage(@NotNull IMessage<T> message) {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        this.executorService.submit(() -> {
            try {
                writeMessageBlocking(message);
                future.complete(null);
            } catch (Throwable ex) {
                future.completeExceptionally(ex);
            }
        });
        return future;
    }

    public @NotNull IMessage<?> readMessageBlocking() throws IOException {
        final ByteBuffer bytes;
        try (Socket socket = new Socket(networkConfig.address(), networkConfig.port());
             InputStream is = socket.getInputStream()) {
            bytes = ByteBuffer.wrap(is.readAllBytes());
        }
        final int messageTypeLen = bytes.getInt();
        final byte[] rawMessageType = new byte[messageTypeLen];
        bytes.get(rawMessageType, 0, messageTypeLen);
        final byte[] data = bytes.slice().array();
        final Optional<? extends IMessageType> messageType = this.messageHandler.findMessageType(rawMessageType);
        if (messageType.isEmpty()) {
            throw new MessageProcessException("Unknown message type: " + new String(rawMessageType, Constants.NETWORK_CHARSET));
        }
        return new RawMessage<>(messageType.get(), data);
    }

    public <T extends IMessageType> void writeMessageBlocking(@NotNull IMessage<T> message) throws IOException {
        final byte[] data = message.data();
        final byte[] messageType = message.messageType().key().networkId();
        final ByteBuffer buffer = ByteBuffer.allocate(data.length + messageType.length + 4);
        buffer.putInt(messageType.length);
        buffer.put(messageType);
        buffer.put(data);
        try (Socket socket = new Socket(networkConfig.address(), networkConfig.port());
             OutputStream os = socket.getOutputStream()) {
            os.write(buffer.array());
        }
    }

}
