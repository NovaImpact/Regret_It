package com.example.regret_it;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection implements Runnable {
    RegretController guiController;

    public ServerConnection(RegretController guiController) {
        this.guiController = guiController;
    }

    public RegretController getGuiController() { return guiController; }
    public void setGuiController(RegretController guiController) { this.guiController = guiController; }

    @Override
    public void run() {
        try {
            ServerSocket myServerSocket = new ServerSocket(6767);
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Server ready at port: " + myServerSocket.getLocalPort());
                Socket newSocket = myServerSocket.accept();

                ObjectOutputStream myObjOutput = new ObjectOutputStream(newSocket.getOutputStream());
                myObjOutput.flush();
                ObjectInputStream myObjInput = new ObjectInputStream(newSocket.getInputStream());

                CommunicationConnection newConnection = new CommunicationConnection(null, newSocket, myObjInput, myObjOutput, null);
                Server.allConnections.add(newConnection);

                synchronized (Server.allChannels) {
                    for (Channel ch : Server.allChannels) {
                        try {
                            myObjOutput.writeObject(ch);
                            myObjOutput.flush();
                        } catch (Exception e) {
                            System.out.println("Failed to send history: " + e);
                        }
                    }
                }

                CommunicationIn newClient = new CommunicationIn(guiController, newConnection);
                Thread perClientThread = new Thread(newClient);
                perClientThread.start();
            }
        } catch (IOException ex) {
            System.out.println("ServerConnector broke: " + ex);
        }
    }
}