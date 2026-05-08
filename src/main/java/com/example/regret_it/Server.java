package com.example.regret_it;


import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Queue;

public class Server{
    public static OutQueue theQueue = new OutQueue();
    public static ChannelQueue chQueue = new ChannelQueue();
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
