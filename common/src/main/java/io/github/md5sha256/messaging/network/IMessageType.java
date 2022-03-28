package io.github.md5sha256.messaging.network;

import io.github.md5sha256.messaging.util.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public interface IMessageType {

    @NotNull
    NamespacedKey key();

}
