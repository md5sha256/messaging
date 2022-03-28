package io.github.md5sha256.messaging.util;

import io.github.md5sha256.messaging.network.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class KeyImpl implements NamespacedKey {

    private final String asString;
    private final byte[] networkId;
    private final String namespace;
    private final String value;

    public KeyImpl(@NotNull String namespace, @NotNull String value) {
        this(namespace, value, ':');
    }

    public KeyImpl(@NotNull String namespace, @NotNull String value, char delim) {
        this.namespace = namespace;
        this.value = value;
        this.asString = namespace + delim + value;
        this.networkId = this.asString.trim().getBytes(Constants.NETWORK_CHARSET);
    }

    @Override
    public @NotNull String namespace() {
        return this.namespace;
    }

    @Override
    public @NotNull String value() {
        return this.value;
    }

    @Override
    public @NotNull String asString(char delim) {
        return this.namespace + delim + this.value;
    }

    @Override
    public @NotNull String asString() {
        return this.asString;
    }

    @Override
    public byte[] networkId() {
        return this.networkId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KeyImpl key = (KeyImpl) o;
        return this.asString.equals(key.asString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.asString);
    }

    @Override
    public String toString() {
        return this.asString;
    }

}
