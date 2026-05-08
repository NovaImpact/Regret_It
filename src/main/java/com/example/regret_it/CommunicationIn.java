package com.example.regret_it;

import java.time.LocalDateTime;

public class CommunicationIn implements Runnable {
    CommunicationConnection myConnection;
    HelloController guiController;

    public CommunicationIn(HelloController guiController, CommunicationConnection myConnection) {
        this.guiController = guiController;
        this.myConnection = myConnection;
    }

    public CommunicationConnection getMyConnection() {
        return myConnection;
    }

    public void setMyConnection(CommunicationConnection myConnection) {
        this.myConnection = myConnection;
    }

    public HelloController getGuiController() {
        return guiController;
    }

    public void setGuiController(HelloController guiController) {
        this.guiController = guiController;
    }

    @Override
    public void run() {
        boolean stayConnected = true;
        while (stayConnected && !Thread.currentThread().isInterrupted()) {
            Message newMessage = null;
            Channel newChannel = null;
            try {
                newMessage = (Message) myConnection.getInStream().readObject();
                newChannel = (Channel) myConnection.getInStream().readObject();
            } catch (Exception ex) {
                System.out.println("CommunicationIn failed connection with:" + myConnection.getName() + ": " + ex);
            }

            if (newMessage != null) {
                System.out.println("CommunicationIn from: " + myConnection.getName() + ": " + newMessage);
                newMessage.setTimeStamp(LocalDateTime.now());
                if (guiController != null) {

                }

                if (newMessage.getMode() == 1) {
                    String newClientName = newMessage.getUser();
                    myConnection.setName(newClientName);
                } else if (newMessage.getMode() == 2  && newMessage.getMode() == 3) {
                    Server.theQueue.put(newMessage);
                }
            }
        }

        System.out.println("CommunicationIn bye: " + myConnection.getName());
    }
}
