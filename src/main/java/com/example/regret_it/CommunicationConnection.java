package com.example.regret_it;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CommunicationConnection {
    private String name;
    private Socket socket;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;
    private Channel channel;

    public CommunicationConnection(String name, Socket socket, ObjectInputStream inStream, ObjectOutputStream outStream, Channel channel) {
        this.name = name;
        this.socket = socket;
        this.inStream = inStream;
        this.outStream = outStream;
        this.channel = channel;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Socket getSocket() { return socket; }
    public void setSocket(Socket socket) { this.socket = socket; }
    public ObjectInputStream getInStream() { return inStream; }
    public void setInStream(ObjectInputStream inStream) { this.inStream = inStream; }
    public ObjectOutputStream getOutStream() { return outStream; }
    public void setOutStream(ObjectOutputStream outStream) { this.outStream = outStream; }
    public Channel getChannel() { return channel; }
    public void setChannel(Channel channel) { this.channel = channel; }
}