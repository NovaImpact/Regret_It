package com.example.regret_it;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class HelloController {
    public TextField username;

    public void initialize() {

    }

    public void connect() {
        try {
            Socket ourSocket = new Socket("10.69.40.225", 67);

            ObjectOutputStream myObjOutput = new ObjectOutputStream(ourSocket.getOutputStream());
            ObjectInputStream myObjInput = new ObjectInputStream(ourSocket.getInputStream());
            CommunicationConnection newConnection = new CommunicationConnection(username.getText(), ourSocket, myObjInput, myObjOutput, null);
            CommunicationIn myCommunicationIn = new CommunicationIn(null, newConnection);
            Thread communicationInThread = new Thread(myCommunicationIn);
            communicationInThread.start();

            Message message1 = new Message(username.getText(),"Hello",null,null, null, null, null, 1);
            myObjOutput.writeObject(message1);
            myObjOutput.flush();
        } catch (Exception ex) {
            System.out.println("Socket failed: " + ex);
        }
    }
}
