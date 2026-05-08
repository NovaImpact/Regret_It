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
    private String currentUsername = "anonymous";
    private ArrayList<Channel> channels = new ArrayList<>();

    @FXML
    public void initialize() {
       // Image logo = new Image(getClass().getResourceAsStream("kermit_reddit_logo_blue_sad.png"));
      //  logoImageView.setImage(logo);
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
            Socket socket = new Socket("127.0.0.1", 67);
            myObjOutput = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream myObjInput = new ObjectInputStream(socket.getInputStream());

            CommunicationConnection connection = new CommunicationConnection(
                    currentUsername, socket, myObjInput, myObjOutput, null);

            CommunicationIn commIn = new CommunicationIn(this, connection);
            Thread t = new Thread(commIn);
            t.setDaemon(true);
            t.start();

            Message hello = new Message(currentUsername, "Hello",
                    null, null, null, null, null, 1);
            myObjOutput.writeObject(hello);
            myObjOutput.flush();

        } catch (Exception ex) {
            System.out.println("Connect failed: " + ex);
        }
    }

    public void onMessageReceived(Message msg) {
        Platform.runLater(() -> {
            if (msg.getMode() == 1 || msg.getMode() == 2) {
                addChannelCard(msg);
            }
        });
    }

    private void addChannelCard(Message msg) {
        String heading = (msg.getChannel() != null && msg.getChannel().getHeading() != null)
                ? msg.getChannel().getHeading()
                : msg.getMessage();

        String time = msg.getTimeStamp() != null
                ? msg.getTimeStamp().format(DateTimeFormatter.ofPattern("h:mm a"))
                : "just now";

        final int[] votes = {0};
        final boolean[] userUpvoted = {false};
        final boolean[] userDownvoted = {false};

        Label voteLabel = new Label("0");
        voteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: #1c1c1c;");

        Button upBtn = new Button("⇧");
        upBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-text-fill: #878a8c; -fx-cursor: hand;");

        Button downBtn = new Button("↓");
        downBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-text-fill: #878a8c; -fx-cursor: hand;");

        upBtn.setOnAction(e -> {
            if (userUpvoted[0]) {
                votes[0]--;
                userUpvoted[0] = false;
                upBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-text-fill: #878a8c; -fx-cursor: hand;");
                voteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: #1c1c1c;");
            } else {
                if (userDownvoted[0]) {
                    votes[0]++;
                    userDownvoted[0] = false;
                    downBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-text-fill: #878a8c; -fx-cursor: hand;");
                }
                votes[0]++;
                userUpvoted[0] = true;
                upBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-text-fill: #FF4500; -fx-cursor: hand;");
                voteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: #FF4500;");
            }
            voteLabel.setText(String.valueOf(votes[0]));
        });

        downBtn.setOnAction(e -> {
            if (userDownvoted[0]) {
                votes[0]++;
                userDownvoted[0] = false;
                downBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-text-fill: #878a8c; -fx-cursor: hand;");
                voteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: #1c1c1c;");
            } else {
                if (userUpvoted[0]) {
                    votes[0]--;
                    userUpvoted[0] = false;
                    upBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-text-fill: #878a8c; -fx-cursor: hand;");
                }
                votes[0]--;
                userDownvoted[0] = true;
                downBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-text-fill: #7193FF; -fx-cursor: hand;");
                voteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: #7193FF;");
            }
            voteLabel.setText(String.valueOf(votes[0]));
        });

        VBox voteBox = new VBox(5, upBtn, voteLabel, downBtn);
        voteBox.setAlignment(Pos.TOP_CENTER);
        voteBox.setMinWidth(44);
        voteBox.setStyle("-fx-background-color: #f8f9fa;");
        VBox.setMargin(upBtn, new Insets(8, 0, 0, 0));

        Label metaLabel = new Label("Posted by u/" + msg.getUser() + " at " + time);
        metaLabel.setStyle("-fx-text-fill: #787c7e; -fx-font-size: 12;");

        Label headingLabel = new Label(heading);
        headingLabel.setStyle("-fx-font-size: 16; -fx-font-weight: 500;");
        headingLabel.setWrapText(true);

        Label commentCountLabel = new Label("0");

        VBox commentsSection = new VBox(6);
        commentsSection.setStyle("-fx-padding: 8 0 0 0;");
        commentsSection.setVisible(false);
        commentsSection.setManaged(false);

        HBox commentInputRow = new HBox(8);
        commentInputRow.setAlignment(Pos.CENTER_LEFT);
        TextField commentField = new TextField();
        commentField.setPromptText("Write a comment...");
        commentField.setStyle("-fx-background-color: #f6f7f8; -fx-border-color: #edeff1; -fx-border-radius: 4;");
        HBox.setHgrow(commentField, Priority.ALWAYS);

        Button submitCommentBtn = new Button("Reply");
        submitCommentBtn.setStyle("-fx-background-color: #0079D3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");

        commentInputRow.getChildren().addAll(commentField, submitCommentBtn);

        VBox commentsList = new VBox(6);

        commentsSection.getChildren().addAll(new Separator(), commentInputRow, commentsList);

        final int[] commentCount = {0};

        submitCommentBtn.setOnAction(e -> {
            String commentText = commentField.getText().trim();
            if (commentText.isEmpty()) return;

            HBox commentCard = buildCommentCard(currentUsername, commentText);
            commentsList.getChildren().add(commentCard);

            commentCount[0]++;
            commentCountLabel.setText(String.valueOf(commentCount[0]));
            commentField.clear();

            if (myObjOutput != null) {
                try {
                    Message commentMsg = new Message(currentUsername, commentText,
                            null, null, null, null, null, 2);
                    myObjOutput.writeObject(commentMsg);
                    myObjOutput.flush();
                } catch (Exception ex) {
                    System.out.println("Comment send failed: " + ex);
                }
            }
        });

        commentField.setOnAction(e -> submitCommentBtn.fire());

        Button commentToggleBtn = new Button();
        commentToggleBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #878a8c; -fx-font-weight: bold; -fx-cursor: hand;");
        commentToggleBtn.textProperty().bind(
                javafx.beans.binding.Bindings.createStringBinding(
                        () -> "💬 " + commentCountLabel.getText() + " Comments",
                        commentCountLabel.textProperty()
                )
        );

        commentToggleBtn.setOnAction(e -> {
            boolean nowVisible = !commentsSection.isVisible();
            commentsSection.setVisible(nowVisible);
            commentsSection.setManaged(nowVisible);
        });

        VBox contentBox = new VBox(6, metaLabel, headingLabel, commentToggleBtn, commentsSection);
        contentBox.setStyle("-fx-padding: 8 12 8 12;");
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        HBox card = new HBox(voteBox, contentBox);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 4; -fx-border-color: #ccc; -fx-border-radius: 4;");

        threadContainer.getChildren().add(0, card);
        updateRankings();
    }

    private HBox buildCommentCard(String user, String text) {
        Label userLabel = new Label("u/" + user);
        userLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: #1c1c1c;");

        Label timeLabel = new Label(" · " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a")));
        timeLabel.setStyle("-fx-text-fill: #787c7e; -fx-font-size: 11;");

        HBox metaRow = new HBox(userLabel, timeLabel);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.setStyle("-fx-font-size: 13;");

        final int[] cVotes = {0};
        final boolean[] cUpvoted = {false};
        final boolean[] cDownvoted = {false};

        Label cVoteLabel = new Label("0");
        cVoteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");

        Button cUp = new Button("⇧");
        cUp.setStyle("-fx-background-color: transparent; -fx-font-size: 13; -fx-text-fill: #878a8c; -fx-cursor: hand;");

        Button cDown = new Button("↓");
        cDown.setStyle("-fx-background-color: transparent; -fx-font-size: 13; -fx-text-fill: #878a8c; -fx-cursor: hand;");

        cUp.setOnAction(e -> {
            if (cUpvoted[0]) {
                cVotes[0]--;
                cUpvoted[0] = false;
                cUp.setStyle("-fx-background-color: transparent; -fx-font-size: 13; -fx-text-fill: #878a8c; -fx-cursor: hand;");
                cVoteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11; -fx-text-fill: #1c1c1c;");
            } else {
                if (cDownvoted[0]) { cVotes[0]++; cDownvoted[0] = false; cDown.setStyle("-fx-background-color: transparent; -fx-font-size: 13; -fx-text-fill: #878a8c; -fx-cursor: hand;"); }
                cVotes[0]++;
                cUpvoted[0] = true;
                cUp.setStyle("-fx-background-color: transparent; -fx-font-size: 13; -fx-text-fill: #FF4500; -fx-cursor: hand;");
                cVoteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11; -fx-text-fill: #FF4500;");
            }
            cVoteLabel.setText(String.valueOf(cVotes[0]));
        });

        cDown.setOnAction(e -> {
            if (cDownvoted[0]) {
                cVotes[0]++;
                cDownvoted[0] = false;
                cDown.setStyle("-fx-background-color: transparent; -fx-font-size: 13; -fx-text-fill: #878a8c; -fx-cursor: hand;");
                cVoteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11; -fx-text-fill: #1c1c1c;");
            } else {
                if (cUpvoted[0]) { cVotes[0]--; cUpvoted[0] = false; cUp.setStyle("-fx-background-color: transparent; -fx-font-size: 13; -fx-text-fill: #878a8c; -fx-cursor: hand;"); }
                cVotes[0]--;
                cDownvoted[0] = true;
                cDown.setStyle("-fx-background-color: transparent; -fx-font-size: 13; -fx-text-fill: #7193FF; -fx-cursor: hand;");
                cVoteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11; -fx-text-fill: #7193FF;");
            }
            cVoteLabel.setText(String.valueOf(cVotes[0]));
        });

        HBox voteRow = new HBox(4, cUp, cVoteLabel, cDown);
        voteRow.setAlignment(Pos.CENTER_LEFT);

        VBox commentContent = new VBox(3, metaRow, textLabel, voteRow);
        commentContent.setStyle("-fx-padding: 6 10 6 10; -fx-background-color: #f6f7f8; -fx-background-radius: 4;");
        HBox.setHgrow(commentContent, Priority.ALWAYS);

        HBox commentCard = new HBox(commentContent);
        commentCard.setPadding(new Insets(2, 0, 2, 12));
        return commentCard;
    }

    private void updateRankings() {
        rankingsBox.getChildren().clear();
        int count = threadContainer.getChildren().size();
        for (int i = 0; i < Math.min(count, 3); i++) {
            Label l = new Label((i + 1) + ". Post #" + (count - i));
            l.setStyle("-fx-font-size: 12;");
            rankingsBox.getChildren().add(l);
        }
    }

    @FXML
    private void onOpenPostPopup() {
        postPopupOverlay.setVisible(true);
    }

    @FXML
    private void onClosePost() {
        postPopupOverlay.setVisible(false);
        postHeadingField.clear();
        postBodyField.clear();
    }

    @FXML
    private void PostThread() {
        String heading = postHeadingField.getText().trim();
        String body = postBodyField.getText().trim();
        if (heading.isEmpty()) return;

        String combined = heading + (body.isEmpty() ? "" : ": " + body);

        try {
            if (myObjOutput != null) {
                Message msg = new Message(currentUsername, combined,
                        null, null, null, null, null, 2);
                myObjOutput.writeObject(msg);
                myObjOutput.flush();
            } else {
                Message local = new Message(currentUsername, combined,
                        null, null, null, LocalDateTime.now(), null, 2);
                addChannelCard(local);
            }
        } catch (Exception ex) {
            System.out.println("PostThread send failed: " + ex);
        }

        onClosePost();
    }

    @FXML private void onBellClicked() { System.out.println("Bell clicked"); }
    @FXML private void FilterThreads() { System.out.println("Filter clicked"); }
    @FXML private void OpenThread()    { System.out.println("Open thread"); }
    @FXML private void Upvote()        { System.out.println("Upvote"); }
    @FXML private void Downvote()      { System.out.println("Downvote"); }
    @FXML private void UploadMedia()   { System.out.println("Upload media"); }
}