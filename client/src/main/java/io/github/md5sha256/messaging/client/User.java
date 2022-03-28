package io.github.md5sha256.messaging.client;

import org.jetbrains.annotations.NotNull;

public class User {

    public static final User LOCAL = new User("You");

    private final String name;

    public User(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String name() {
        return this.name;
    }

    public final boolean isLocal() {
        return this == LOCAL;
    }

    public final boolean isRemote() {
        return this != LOCAL;
    }

}
