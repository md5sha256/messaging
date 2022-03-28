package io.github.md5sha256.messaging.client;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

public class ChatHistoryAccessor {

    private final NavigableMap<Long, ChatMessage> timestampMessageLookup = new TreeMap<>();

    public void addMessage(@NotNull ChatMessage message) {
        timestampMessageLookup.put(message.timestamp(), message);
    }

    public @NotNull List<ChatMessage> newestMessages() {
        return new ArrayList<>(timestampMessageLookup.descendingMap().values());
    }

    public @NotNull List<ChatMessage> oldestMessages() {
        return new ArrayList<>(timestampMessageLookup.values());
    }

    public @NotNull Stream<ChatMessage> messageStream() {
        return timestampMessageLookup.values().stream();
    }

    public @NotNull Stream<ChatMessage> reverseMessageStream() {
        return timestampMessageLookup.descendingMap().values().stream();
    }


    public @NotNull List<ChatMessage> lookupMessages(final long duration) {
        long now = Instant.now().getEpochSecond();
        long oldest = now - duration;
        final List<ChatMessage> messages = new LinkedList<>();
        for (ChatMessage message : timestampMessageLookup.descendingMap().values()) {
            if (message.timestamp() < oldest) {
                break;
            }
            messages.add(message);
        }
        return messages;
    }

    public @NotNull List<ChatMessage> lookupRecent(final int numMessages) {
        int i = 0;
        final List<ChatMessage> messages = new ArrayList<>(numMessages);
        for (ChatMessage message : this.timestampMessageLookup.descendingMap().values()) {
            messages.add(message);
            i++;
            if (i >= numMessages) {
                break;
            }
        }
        return messages;
    }

}
