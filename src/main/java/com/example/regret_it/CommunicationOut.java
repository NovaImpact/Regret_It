package com.example.regret_it;

import java.io.IOException;
import java.util.ArrayList;

public class CommunicationOut implements Runnable{
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Message message = Server.theQueue.get();
            while (message == null) {
                message = Server.theQueue.get();
            }
            ArrayList<CommunicationConnection> connectionsToDisconnect = new ArrayList<>();
            for (CommunicationConnection eachConnection : Server.allConnections) {
                try {
                    eachConnection.getOutStream().writeObject(message);
                    eachConnection.getOutStream().flush();
                    System.out.println("CommunicationOut  to: " + eachConnection.getName() + ": " + message);

                    if (message.getUser().equalsIgnoreCase(eachConnection.getName()) && message.getMode() == 3){
                        connectionsToDisconnect.add(eachConnection);
                    }
                } catch (IOException e) {
                    System.out.println("CommunicationOut failed connection to: " + eachConnection.getName() + ": " + e);
                }
            }
            for (CommunicationConnection disconnectedConnection : connectionsToDisconnect) {
                Server.allConnections.remove(disconnectedConnection);
            }
        }
    }
}
