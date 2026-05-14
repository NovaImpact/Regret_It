package com.example.regret_it;

import java.io.IOException;
import java.util.ArrayList;

public class CommunicationOut implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Channel channel = Server.chQueue.get();
            Message message = Server.theQueue.get();

            if (channel != null) {
                ArrayList<CommunicationConnection> connectionsToDisconnect = new ArrayList<>();
                for (CommunicationConnection eachConnection : new ArrayList<>(Server.allConnections)) {
                    try {
                        eachConnection.getOutStream().writeObject(channel);
                        eachConnection.getOutStream().flush();
                        eachConnection.getOutStream().reset();
                        System.out.println("CommunicationOut channel to: " + eachConnection.getName());
                    } catch (IOException e) {
                        System.out.println("CommunicationOut failed to: " + eachConnection.getName() + ": " + e);
                        connectionsToDisconnect.add(eachConnection);
                    }
                }
                Server.allConnections.removeAll(connectionsToDisconnect);
            }

            if (message != null && message.getMode() == 3) {
                ArrayList<CommunicationConnection> connectionsToDisconnect = new ArrayList<>();
                for (CommunicationConnection eachConnection : new ArrayList<>(Server.allConnections)) {
                    if (message.getUser().equalsIgnoreCase(eachConnection.getName())) {
                        connectionsToDisconnect.add(eachConnection);
                    }
                }
                Server.allConnections.removeAll(connectionsToDisconnect);
            }

            if (channel == null && message == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}