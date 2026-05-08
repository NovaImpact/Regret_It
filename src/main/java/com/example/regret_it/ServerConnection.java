package com.example.regret_it;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection implements Runnable{
    RegretController guiController;

    public ServerConnection(RegretController guiController) {
        this.guiController = guiController;
    }

    public RegretController getGuiController() {
        return guiController;
    }

    public void setGuiController(RegretController guiController) {
        this.guiController = guiController;
    }

    @Override
    public void run() {
        try {
            ServerSocket myServerSocket = new ServerSocket(12);
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Server ready at port: " + myServerSocket.getLocalPort());
                Socket newSocket = myServerSocket.accept();

                ObjectInputStream myObjInput = new ObjectInputStream(newSocket.getInputStream());
                ObjectOutputStream myObjOutput = new ObjectOutputStream(newSocket.getOutputStream());
                CommunicationConnection newConnection = new CommunicationConnection(null, newSocket, myObjInput, myObjOutput, null);
                Server.allConnections.add(newConnection);

                CommunicationIn newClient = new CommunicationIn(guiController, newConnection);
                Thread perClientThread = new Thread(newClient);
                perClientThread.start();
            }
        } catch (IOException ex) {
            System.out.println("ServerConnector broke: " + ex );
        }
    }
}
