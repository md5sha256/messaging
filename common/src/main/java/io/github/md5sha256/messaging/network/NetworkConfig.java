package io.github.md5sha256.messaging.network;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public record NetworkConfig(@NotNull InetAddress address, int port) {

}
