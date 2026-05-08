package com.example.regret_it;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class HelloController {
    public StackPane usernamePopupOverlay;
    public TextField usernameInputField;



    private ObjectOutputStream myObjOutput;
    private ObjectInputStream myObjInput;

    public void initialize() {

    }

    public void onBellClicked() {

    }

    public void FilterThreads() {

    }

    public void Upvote() {

    }

    public void Downvote() {

    }

    public void OpenThread() {

    }

    public void onOpenPostPopup() {

    }

    public void onClosePost() {

    }

    public void UploadMedia() {

    }

    public void PostThread() {

    }

    public void onSetUsername() {
        try {
            Socket ourSocket = new Socket("10.69.40.225", 12);

            myObjOutput = new ObjectOutputStream(ourSocket.getOutputStream());
            myObjInput = new ObjectInputStream(ourSocket.getInputStream());
            CommunicationConnection newConnection = new CommunicationConnection(usernameInputField.getText(), ourSocket, myObjInput, myObjOutput, null);
            CommunicationIn myCommunicationIn = new CommunicationIn(null, newConnection);
            Thread communicationInThread = new Thread(myCommunicationIn);
            communicationInThread.start();

            Message message1 = new Message(usernameInputField.getText(),"Hello",null,null, null, null, null, 1);
            myObjOutput.writeObject(message1);
            myObjOutput.flush();
            usernamePopupOverlay.setVisible(false);
            usernamePopupOverlay.setDisable(true);
        } catch (Exception ex) {
            System.out.println("Socket failed: " + ex);
        }
    }
}
