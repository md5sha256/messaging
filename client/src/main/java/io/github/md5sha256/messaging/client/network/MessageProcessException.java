package io.github.md5sha256.messaging.client.network;

import java.io.IOException;

public class MessageProcessException extends IOException {

    public MessageProcessException() {
    }

    public MessageProcessException(String s) {
        super(s);
    }

    public MessageProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageProcessException(Throwable cause) {
        super(cause);
    }

}
