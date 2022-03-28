package io.github.md5sha256.messaging.client;

import io.github.md5sha256.messaging.client.network.NetworkClient;
import io.github.md5sha256.messaging.network.NetworkConfig;
import io.github.md5sha256.messaging.network.TextMessage;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.fxmisc.flowless.Cell;
import org.fxmisc.flowless.VirtualFlow;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class Client extends Application {

    private NetworkClient networkClient;
    private Label info;
    private ObservableList<ChatMessage> chatCache;
    private ChatHistoryAccessor chatHistoryAccessor = new ChatHistoryAccessor();

    public static void main(String[] args) {
        Client.launch(Client.class);
    }

    @Override
    public void init() throws Exception {
        final InetAddress localHost = InetAddress.getLocalHost();
        this.networkClient = new NetworkClient(new NetworkConfig(localHost, 25565));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final VBox vBox = initStage();
        final Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private VBox initStage() {
        final VBox root = new VBox();
        final SplitPane pane = new SplitPane();
        pane.setOrientation(Orientation.VERTICAL);
        root.heightProperty().addListener(((observable, oldValue, newValue) -> pane.setPrefHeight(newValue.doubleValue())));
        this.info = new Label();
        setupChatHistory(pane);
        setupTextInput(pane);
        root.getChildren().addAll(this.info, pane);
        return root;
    }

    private void setupChatHistory(@NotNull SplitPane root) {
        final Client client = this;
        this.chatCache = FXCollections.observableList(new LinkedList<>());
        final VirtualFlow<ChatMessage, Cell<ChatMessage, Node>> flow
                = VirtualFlow.createVertical(this.chatCache, client::createChatHistoryCell, VirtualFlow.Gravity.REAR);
        flow.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        root.heightProperty().addListener((observable, oldValue, newValue) -> flow.setPrefHeight(newValue.doubleValue() * 0.8));
        root.getItems().add(flow);
    }

    private void setupTextInput(@NotNull final SplitPane root) {
        final TextInputControl textInput = new TextField();
        textInput.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                final String msg = textInput.getText();
                System.out.println("Sending message: " + msg);
                recordMessage(msg, User.LOCAL);
                sendMessage(msg);
                textInput.clear();
            }
        });
        root.getItems().add(textInput);
    }

    private void recordMessage(@NotNull String message, @NotNull User sender) {
        final ChatMessage chatMessage = new ChatMessage(message, Instant.now().getEpochSecond(), sender);
        this.chatHistoryAccessor.addMessage(chatMessage);
        this.chatCache.add(chatMessage);
    }

    private Cell<ChatMessage, Node> createChatHistoryCell(@NotNull ChatMessage message) {
        final VBox node = new VBox();
        final Text content = new Text(message.content());
        content.setFont(Font.font(14));
        final Text sender = new Text(message.sender().name() + " ");
        sender.setFont(Font.font("roboto", 18));
        sender.setFill(Color.WHITE);
        final Text date = new Text(formatDate(message.timestamp()));
        date.setFill(Color.LIGHTGRAY);
        date.setFont(Font.font("roboto", 14));
        final TextFlow header = new TextFlow(sender, date);
        node.getChildren().addAll(header, content);
        content.setFont(Font.font("roboto", 14));
        content.setFill(Color.WHITE);
        content.setText(message.content());
        node.setPadding(new Insets(20, 0, 10, 10));
        return () -> node;
    }

    private String formatDate(long time) {
        final Calendar now = new GregorianCalendar();
        final Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(Instant.ofEpochSecond(time).toEpochMilli());
        final DateFormat format;
        final Date date = Date.from(calendar.toInstant());
        if (now.get(Calendar.YEAR) != calendar.get(Calendar.YEAR)) {
            format = new SimpleDateFormat("dd/MM/yyyy");
            return format.format(date);
        }
        if (now.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) {
            format = new SimpleDateFormat("dd/MM");
            return format.format(date);
        }
        int dayDiff = now.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH);
        return switch (dayDiff) {
            case 7 -> new SimpleDateFormat("dd/MM").format(date);
            case 6, 5, 4, 3, 2 -> new SimpleDateFormat("EE").format(date);
            case 1 -> "Yesterday at " + new SimpleDateFormat("hh:mm").format(date);
            case 0 -> "Today at " + new SimpleDateFormat("hh:mm").format(date);
            default -> throw new IllegalStateException("Don't know how to parse time: " + new SimpleDateFormat("hh:mm:ss dd/MM/yyyy").format(Date.from(calendar.toInstant())));
        };

    }


    private void sendMessage(@NotNull String message) {
        this.networkClient.sendMessage(new TextMessage(message));
    }

}
