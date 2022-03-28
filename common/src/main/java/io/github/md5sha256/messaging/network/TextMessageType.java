package io.github.md5sha256.messaging.network;

import io.github.md5sha256.messaging.util.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class TextMessageType implements IMessageType {

    public static final TextMessageType INSTANCE = new TextMessageType();
    private final NamespacedKey key;

    private TextMessageType() {
        this.key = NamespacedKey.of(Constants.USER_INPUT_NAMESPACE, "text-input");
    }

    @Override
    public @NotNull NamespacedKey key() {
        return this.key;
    }

}
