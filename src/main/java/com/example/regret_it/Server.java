package com.example.regret_it;

import java.io.*;
import java.util.ArrayList;

public class Server {
    public static OutQueue theQueue = new OutQueue();
    public static ChannelQueue chQueue = new ChannelQueue();
    public static ArrayList<CommunicationConnection> allConnections = new ArrayList<>();
    public static ArrayList<Channel> allChannels = new ArrayList<>();

    private static final String SAVE_FILE = "regretit_posts.dat";

    public static synchronized void saveChannels() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(allChannels);
        } catch (Exception e) {
            System.out.println("Save failed: " + e);
        }
    }

    @SuppressWarnings("unchecked")
    public static synchronized void loadChannels() {
        File f = new File(SAVE_FILE);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof ArrayList) {
                allChannels = (ArrayList<Channel>) obj;
            }
        } catch (Exception e) {
            System.out.println("Load failed: " + e);
        }
    }

    public static void main(String[] args) {
        loadChannels();
        System.out.println("Loaded " + allChannels.size() + " saved posts.");

        ServerConnection myServerConnector = new ServerConnection(null);
        Thread myServerConnectorThread = new Thread(myServerConnector);
        myServerConnectorThread.start();

        CommunicationOut myCommunicationOut = new CommunicationOut();
        Thread myCommunicationOutThread = new Thread(myCommunicationOut);
        myCommunicationOutThread.start();
    }
}