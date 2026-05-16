package com.example.regret_it;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegretController {

    @FXML private VBox threadContainer;
    @FXML private StackPane postPopupOverlay;
    @FXML private StackPane usernamePopupOverlay;
    @FXML private TextField usernameInputField;
    @FXML private TextField postHeadingField;
    @FXML private TextArea postBodyField;
    @FXML private ComboBox<String> userDropdown;
    @FXML private VBox rankingsBox;

    @FXML private Button postFabButton;

    private volatile ObjectOutputStream myObjOutput;
    private String currentUsername = "anonymous";
    private final Map<String, HBox> postCardMap = new HashMap<>();
    private final Map<String, Label> voteLabels = new HashMap<>();

    private FileChooser fileChooser = new FileChooser();
    private File selectedFile;

    @FXML
    public void initialize() {
        if (postFabButton != null) postFabButton.setDisable(true);
    }

    @FXML
    private void onSetUsername() {
        String name = usernameInputField.getText().trim();
        if (name.isEmpty()) return;

        currentUsername = name;
        userDropdown.getItems().clear();
        userDropdown.getItems().add("u/" + currentUsername);
        userDropdown.getSelectionModel().selectFirst();
        usernamePopupOverlay.setVisible(false);

        Thread connectThread = new Thread(() -> connectToServer());
        connectThread.setDaemon(true);
        connectThread.start();
    }

    private void connectToServer() {
        try {
            Socket ourSocket = new Socket("10.0.0.195", 5567);
            ObjectOutputStream out = new ObjectOutputStream(ourSocket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(ourSocket.getInputStream());
            myObjOutput = out;
            Platform.runLater(() -> { if (postFabButton != null) postFabButton.setDisable(false); });

            CommunicationConnection newConnection = new CommunicationConnection(currentUsername, ourSocket, in, out, null);

            CommunicationIn myCommunicationIn = new CommunicationIn(this, newConnection);
            Thread communicationInThread = new Thread(myCommunicationIn);
            communicationInThread.setDaemon(true);
            communicationInThread.start();

            Message loginMsg = new Message(currentUsername, "Joined", LocalDateTime.now(), null, 1);
            out.writeObject(loginMsg);
            out.flush();

        } catch (Exception ex) {
            System.out.println("Socket failed: " + ex);
        }
    }

    public void onMessageReceived(Message msg) {
    }

    public void onChannelReceived(Channel channel) {
        Platform.runLater(() -> upsertChannelCard(channel));
    }

    private void upsertChannelCard(Channel channel) {
        String postId = channel.getPostId();

        if (postId != null && postCardMap.containsKey(postId)) {
            Label vl = voteLabels.get(postId);
            if (vl != null) {
                vl.setText(String.valueOf(channel.getScore()));
            }
            return;
        }

        Message msg = channel.getMessage();
        if (msg == null) return;

        VBox voteBox = new VBox(5);
        voteBox.setAlignment(Pos.TOP_CENTER);
        voteBox.setMinWidth(44);
        voteBox.setStyle("-fx-background-color: #f8f9fa;");

        Button upBtn = new Button("⇧");
        upBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-text-fill: #878a8c; -fx-cursor: hand;");

        Label voteLabel = new Label(String.valueOf(channel.getScore()));
        voteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");

        Button downBtn = new Button("↓");
        downBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-text-fill: #878a8c; -fx-cursor: hand;");

        if (postId != null) {
            voteLabels.put(postId, voteLabel);
        }

        upBtn.setOnAction(e -> sendVote(channel.getPostId(), "up"));
        downBtn.setOnAction(e -> sendVote(channel.getPostId(), "down"));

        voteBox.getChildren().addAll(upBtn, voteLabel, downBtn);
        VBox.setMargin(upBtn, new Insets(8, 0, 0, 0));

        VBox contentBox = new VBox(8);
        contentBox.setPadding(new Insets(8, 12, 8, 12));
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        String time = (msg.getTimeStamp() != null)
                ? msg.getTimeStamp().format(DateTimeFormatter.ofPattern("h:mm a"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a"));

        Label metaLabel = new Label("Posted by u/" + msg.getUser() + "  •  " + time);
        metaLabel.setStyle("-fx-text-fill: #787c7e; -fx-font-size: 12;");

        Label bodyLabel = new Label(msg.getMessage());
        bodyLabel.setStyle("-fx-font-size: 16; -fx-font-weight: 500;");
        bodyLabel.setWrapText(true);

        HBox footer = new HBox(15);
        Button commentBtn = new Button("💬 Comments");
        commentBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #878a8c; -fx-font-weight: bold;");
        footer.getChildren().add(commentBtn);

        contentBox.getChildren().addAll(metaLabel, bodyLabel, footer);

        HBox card = new HBox(voteBox, contentBox);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 4; -fx-border-color: #ccc; -fx-border-radius: 4;");

        if (postId != null) {
            postCardMap.put(postId, card);
        }

        threadContainer.getChildren().add(0, card);
        updateRankings();
    }

    private void sendVote(String postId, String voteType) {
        if (myObjOutput == null || postId == null) return;
        Thread t = new Thread(() -> {
            try {
                Message voteMsg = new Message(currentUsername, voteType, LocalDateTime.now(), null, 4);
                voteMsg.setPostId(postId);
                synchronized (myObjOutput) {
                    myObjOutput.writeObject(voteMsg);
                    myObjOutput.flush();
                }
            } catch (Exception ex) {
                System.out.println("Vote send failed: " + ex);
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void updateRankings() {
        rankingsBox.getChildren().clear();
        int count = threadContainer.getChildren().size();
        for (int i = 0; i < Math.min(count, 5); i++) {
            Label l = new Label((i + 1) + ". Recent Confession #" + (count - i));
            l.setStyle("-fx-font-size: 12; -fx-padding: 2;");
            rankingsBox.getChildren().add(l);
        }
    }

    @FXML
    private void PostThread() {
        if (myObjOutput == null) return;
        String heading = postHeadingField.getText().trim();
        String body = postBodyField.getText().trim();
        if (heading.isEmpty()) return;

        String combinedText = heading + (body.isEmpty() ? "" : "\n" + body);
        String postId = UUID.randomUUID().toString();

        Thread t = new Thread(() -> {
            try {
                Message msg = new Message(currentUsername, combinedText, LocalDateTime.now(), null, 2);
                byte[] mediaData = Files.readAllBytes(selectedFile.toPath());
                Channel channel = new Channel(msg, heading, new ArrayList<Message>(), 0, 0, new ArrayList<String>(), postId, mediaData, selectedFile.getName());
                synchronized (myObjOutput) {
                    myObjOutput.writeObject(channel);
                    myObjOutput.flush();
                }
            } catch (Exception ex) {
                System.out.println("Post failed: " + ex);
            }
        });
        t.setDaemon(true);
        t.start();

        onClosePost();
    }

    @FXML private void onOpenPostPopup() { postPopupOverlay.setVisible(true); }

    @FXML private void onClosePost() {
        postPopupOverlay.setVisible(false);
        postHeadingField.clear();
        postBodyField.clear();
    }

    @FXML private void onBellClicked() {}
    @FXML private void FilterThreads() {}
    @FXML private void OpenThread() {}
    @FXML private void Upvote() {}
    @FXML private void Downvote() {}
    @FXML private void UploadMedia() {
        selectedFile = null;
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Media Files", "*.png", "*.jpg", "*.jpeg", "*.mp2", "*.mp3")
        );
        selectedFile = fileChooser.showOpenDialog(null);
    }
}