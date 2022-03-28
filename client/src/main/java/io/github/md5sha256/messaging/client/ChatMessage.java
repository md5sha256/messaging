package io.github.md5sha256.messaging.client;

import org.jetbrains.annotations.NotNull;

public class ChatMessage {

    private final String content;
    private final long timestamp;
    private final User user;

    public ChatMessage(@NotNull String content, long timestamp, @NotNull User sender) {
        this.content = content;
        this.timestamp = timestamp;
        this.user = sender;
    }

    public @NotNull String content() {
        return this.content;
    }

    public long timestamp() {
        return this.timestamp;
    }

    public @NotNull User sender() {
        return this.user;
    }

}
