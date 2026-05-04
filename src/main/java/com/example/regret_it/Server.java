package com.example.regret_it;


import java.util.ArrayList;

public class Server{
    static ArrayList<CommunicationConnection> allConnections = new ArrayList<>();

    public static void main(String[] args)  {
        ServerConnection myServerConnector =  new ServerConnection(null);
        Thread myServerConnectorThread = new Thread(myServerConnector);
        myServerConnectorThread.start();

        CommunicationOut myCommunicationOut = new CommunicationOut();
        Thread myCommunicationOutThread = new Thread(myCommunicationOut);
        myCommunicationOutThread.start();
    }
}
