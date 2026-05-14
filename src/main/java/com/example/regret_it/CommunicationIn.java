package com.example.regret_it;

import java.time.LocalDateTime;

public class CommunicationIn implements Runnable {
    CommunicationConnection myConnection;
    RegretController guiController;

    public CommunicationIn(RegretController guiController, CommunicationConnection myConnection) {
        this.guiController = guiController;
        this.myConnection = myConnection;
    }

    public CommunicationConnection getMyConnection() { return myConnection; }
    public void setMyConnection(CommunicationConnection myConnection) { this.myConnection = myConnection; }
    public RegretController getGuiController() { return guiController; }
    public void setGuiController(RegretController guiController) { this.guiController = guiController; }

    private boolean isServerSide() {
        return Server.allConnections != null && Server.allConnections.contains(myConnection);
    }

    @Override
    public void run() {
        boolean stayConnected = true;
        while (stayConnected && !Thread.currentThread().isInterrupted()) {
            try {
                Object obj = myConnection.getInStream().readObject();

                if (obj instanceof Message) {
                    Message newMessage = (Message) obj;
                    if (newMessage.getTimeStamp() == null) {
                        newMessage.setTimeStamp(LocalDateTime.now());
                    }
                    System.out.println("CommunicationIn from: " + myConnection.getName() + ": " + newMessage);

                    if (isServerSide()) {
                        if (newMessage.getMode() == 1) {
                            myConnection.setName(newMessage.getUser());

                        } else if (newMessage.getMode() == 2) {
                            Channel newChannel = new Channel(
                                    newMessage,
                                    newMessage.getMessage(),
                                    null, 0, 0, null,
                                    newMessage.getPostId()
                            );
                            synchronized (Server.allChannels) {
                                Server.allChannels.add(newChannel);
                            }
                            Server.saveChannels();
                            Server.chQueue.put(newChannel);

                        } else if (newMessage.getMode() == 3) {
                            stayConnected = false;
                            Server.theQueue.put(newMessage);

                        } else if (newMessage.getMode() == 4) {
                            handleVote(newMessage);
                        }
                    }

                    if (guiController != null) {
                        guiController.onMessageReceived(newMessage);
                    }

                    if (newMessage.getMode() == 3) {
                        stayConnected = false;
                    }

                } else if (obj instanceof Channel) {
                    Channel newChannel = (Channel) obj;
                    System.out.println("CommunicationIn from: " + myConnection.getName() + ": " + newChannel);

                    if (guiController != null) {
                        guiController.onChannelReceived(newChannel);
                    }
                }

            } catch (Exception ex) {
                System.out.println("CommunicationIn lost connection with: " + myConnection.getName() + ": " + ex);
                stayConnected = false;
            }
        }
        System.out.println("CommunicationIn bye: " + myConnection.getName());
        if (isServerSide()) {
            Server.allConnections.remove(myConnection);
        }
    }

    private void handleVote(Message voteMsg) {
        String postId = voteMsg.getPostId();
        String voteType = voteMsg.getMessage();
        synchronized (Server.allChannels) {
            for (Channel ch : Server.allChannels) {
                if (postId != null && postId.equals(ch.getPostId())) {
                    if ("up".equals(voteType)) {
                        ch.setUpVote(ch.getUpVote() + 1);
                    } else if ("down".equals(voteType)) {
                        ch.setDownVote(ch.getDownVote() + 1);
                    }
                    Server.saveChannels();
                    Server.chQueue.put(ch);
                    break;
                }
            }
        }
    }
}