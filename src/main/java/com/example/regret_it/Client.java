package com.example.regret_it;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket ourSocket = new Socket("10.69.40.225", 67);

            ObjectOutputStream myObjOutput = new ObjectOutputStream(ourSocket.getOutputStream());
            ObjectInputStream myObjInput = new ObjectInputStream(ourSocket.getInputStream());
            CommunicationConnection newConnection = new CommunicationConnection("Test", ourSocket, myObjInput, myObjOutput, null);
            CommunicationIn myCommunicationIn = new CommunicationIn(null, newConnection);
            Thread communicationInThread = new Thread(myCommunicationIn);
            communicationInThread.start();

            Message message1 = new Message("Tai","Hello",null,null, null, null, null, 1);
            myObjOutput.writeObject(message1);
            myObjOutput.flush();

            Scanner inputTextScanner = new Scanner(System.in);
            boolean keepScanning = true;
            while (keepScanning) {
                System.out.print("Type your message: ");
                String theText = inputTextScanner.nextLine();
                if (theText.equalsIgnoreCase("STOP")) {
                    keepScanning = false;
                } else {
                    Message newMessage = new Message("Tai", theText, null, null, null, null, null, 2);
                    myObjOutput.writeObject(newMessage);
                    myObjOutput.flush();
                }
            }

            Message message3 = new Message("Tai", "", null, null, null, null, null, 3);
            myObjOutput.writeObject(message3);
            myObjOutput.flush();
            communicationInThread.interrupt();
        } catch (Exception ex) {
            System.out.println("Socket failed: " + ex);
        }
        System.out.println("Client.main DONE");
    }
}
