package com.example.regret_it;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RegretController {

    @FXML private VBox threadContainer;
    @FXML private StackPane postPopupOverlay;
    @FXML private StackPane usernamePopupOverlay;
    @FXML private TextField usernameInputField;
    @FXML private TextField postHeadingField;
    @FXML private TextArea postBodyField;
    @FXML private ComboBox<String> userDropdown;
    @FXML private VBox rankingsBox;
    @FXML private ImageView logoImageView;

    private ObjectOutputStream myObjOutput;
    private ObjectInputStream myObjInput;
    private String currentUsername = "anonymous";

    @FXML
    public void initialize() {
        // Logo loading logic can be placed here if needed
    }

    @FXML
    private void onSetUsername() {
        String name = usernameInputField.getText().trim();
        if (name.isEmpty()) return;

        currentUsername = name;
        userDropdown.getItems().clear();
        userDropdown.getItems().add("u/" + currentUsername);
        userDropdown.getSelectionModel().selectFirst();

        connectToServer();
        usernamePopupOverlay.setVisible(false);
    }

    private void connectToServer() {
        try {
            Socket ourSocket = new Socket("10.69.40.225", 5528);
            myObjOutput = new ObjectOutputStream(ourSocket.getOutputStream());
            myObjInput = new ObjectInputStream(ourSocket.getInputStream());

            CommunicationConnection newConnection = new CommunicationConnection(currentUsername, ourSocket, myObjInput, myObjOutput, null);

            // Start the listener thread - Pass 'this' so it can call onMessageReceived
            CommunicationIn myCommunicationIn = new CommunicationIn(this, newConnection);
            Thread communicationInThread = new Thread(myCommunicationIn);
            communicationInThread.setDaemon(true);
            communicationInThread.start();

            // Notify server of join
            Message loginMsg = new Message(currentUsername, "Joined", null, null, null, null, null, 1);
            myObjOutput.writeObject(loginMsg);
            myObjOutput.flush();
        } catch (Exception ex) {
            System.out.println("Socket failed: " + ex);
        }
    }

    // --- RECEIVE LOGIC ---

    public void onMessageReceived(Message msg) {
        // Mode 2 is a post/confession
        if (msg.getMode() == 2) {
            Platform.runLater(() -> addChannelCard(msg));
        }
    }

    public void onThreadReceived(Channel channel) {
        // Use the message inside the channel to build the card
        if (channel.getMessage() != null) {
            Platform.runLater(() -> addChannelCard(channel.getMessage()));
        }
    }



    private void addChannelCard(Message msg) {
        // 1. Voting Sidebar (Left)
        VBox voteBox = new VBox(5);
        voteBox.setAlignment(Pos.TOP_CENTER);
        voteBox.setMinWidth(44);
        voteBox.setStyle("-fx-background-color: #f8f9fa;");

        Button upBtn = new Button("⇧");
        upBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-text-fill: #878a8c; -fx-cursor: hand;");
        Label voteLabel = new Label("0");
        voteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        Button downBtn = new Button("↓");
        downBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-text-fill: #878a8c; -fx-cursor: hand;");

        voteBox.getChildren().addAll(upBtn, voteLabel, downBtn);
        VBox.setMargin(upBtn, new Insets(8, 0, 0, 0));

        // 2. Content Area (Right)
        VBox contentBox = new VBox(8);
        contentBox.setPadding(new Insets(8, 12, 8, 12));
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        String time = (msg.getTimeStamp() != null)
                ? msg.getTimeStamp().format(DateTimeFormatter.ofPattern("h:mm a"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a"));

        Label metaLabel = new Label("Posted by u/" + msg.getUser() + " • " + time);
        metaLabel.setStyle("-fx-text-fill: #787c7e; -fx-font-size: 12;");

        Label bodyLabel = new Label(msg.getMessage());
        bodyLabel.setStyle("-fx-font-size: 16; -fx-font-weight: 500;");
        bodyLabel.setWrapText(true);

        // Footer
        HBox footer = new HBox(15);
        Button commentBtn = new Button("💬 Comments");
        commentBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #878a8c; -fx-font-weight: bold;");
        footer.getChildren().add(commentBtn);

        contentBox.getChildren().addAll(metaLabel, bodyLabel, footer);

        // 3. Assemble and Add to Feed
        HBox card = new HBox(voteBox, contentBox);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 4; -fx-border-color: #ccc; -fx-border-radius: 4;");

        threadContainer.getChildren().add(0, card);
        updateRankings();
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

    // --- SEND LOGIC ---

    @FXML
    private void PostThread() {
        String heading = postHeadingField.getText().trim();
        String body = postBodyField.getText().trim();
        if (heading.isEmpty()) return;

        String combinedText = heading + (body.isEmpty() ? "" : "\n" + body);

        try {

            Message msg = new Message(currentUsername, combinedText, null, null, null, LocalDateTime.now(), null, 2);
            myObjOutput.writeObject(msg);
            myObjOutput.flush();
            onClosePost();
        } catch (Exception ex) {
            System.out.println("Post failed: " + ex);
        }
    }

    // --- UI HELPERS ---

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
    @FXML private void UploadMedia() {}
}