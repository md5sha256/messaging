package io.github.md5sha256.messaging.util;

import org.jetbrains.annotations.NotNull;

public interface Keyed {

    @NotNull NamespacedKey key();

}
