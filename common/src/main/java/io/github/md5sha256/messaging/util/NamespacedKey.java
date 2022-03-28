package io.github.md5sha256.messaging.util;

import org.jetbrains.annotations.NotNull;

public interface NamespacedKey {

    static NamespacedKey of(@NotNull String namespace, @NotNull String value) {
        return new KeyImpl(namespace, value);
    }

    static NamespacedKey of(@NotNull String namespace, @NotNull String value, char delim) {
        return new KeyImpl(namespace, value, delim);
    }

    static @NotNull NamespacedKey from(@NotNull String full) {
        return from(full, ':');
    }

    static @NotNull NamespacedKey from(@NotNull String full, char delim) {
        final String[] split = full.split(Character.toString(delim));
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid key: " + full);
        }
        return new KeyImpl(split[0], split[1], delim);
    }

    @NotNull String namespace();

    @NotNull String value();

    @NotNull String asString();

    @NotNull String asString(char delim);

    @NotNull byte[] networkId();

}
