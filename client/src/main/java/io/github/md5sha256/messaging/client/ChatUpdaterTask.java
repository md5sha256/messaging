package io.github.md5sha256.messaging.client;

import io.github.md5sha256.messaging.client.network.NetworkClient;
import io.github.md5sha256.messaging.network.IMessage;
import io.github.md5sha256.messaging.network.TextMessage;
import io.github.md5sha256.messaging.network.TextMessageType;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ChatUpdaterTask implements Runnable {

    @SuppressWarnings("rawtypes")
    private TextFlow textFlow;

    private NetworkClient networkClient;

    ChatUpdaterTask(@NotNull TextFlow textFlow, @NotNull NetworkClient networkClient) {
        this.textFlow = textFlow;
        this.networkClient = networkClient;
    }

    @Override
    public void run() {
        List<String> messages = pollMessages();
        if (!messages.isEmpty()) {
            Platform.runLater(() -> {
                final ObservableList<Node> children = this.textFlow.getChildren();
                for (@NotNull final String msg : messages) {
                    children.add(new Text(msg));
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private @NotNull List<String> pollMessages() {
        try {
            final IMessage<?> message = this.networkClient.readMessageBlocking();
            if (message.messageType() != TextMessageType.INSTANCE) {
                return Collections.emptyList();
            }
            return Collections.singletonList(new TextMessage((IMessage<TextMessageType>) message).getText());
        } catch (IOException ex) {
            return Collections.emptyList();
        }
    }

}
