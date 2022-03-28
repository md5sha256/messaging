package io.github.md5sha256.messaging.server;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final ServerSocket serverSocket;


    public Server(int port, int maxConnections) throws IOException {
        this.serverSocket = new ServerSocket(port, maxConnections);
    }

    public byte[] processConnection() throws IOException {
        final Socket socket = this.serverSocket.accept();
        return readMessageBlocking(socket);
    }

    public byte[] readMessageBlocking(@NotNull Socket socket) throws IOException {
        try (InputStream is = socket.getInputStream()) {
            return is.readAllBytes();
        }
    }

    public void writeMessageBlocking(@NotNull Socket socket, byte[] message) throws IOException {
        try (OutputStream os = socket.getOutputStream()) {
            os.write(message);
        }
    }

}
