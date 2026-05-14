package com.example.regret_it;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String user;
    private String message;
    private LocalDateTime timeStamp;
    private Channel channel;
    private Integer mode;
    private String postId;

    public Message(String user, String message, LocalDateTime timeStamp, Channel channel, Integer mode) {
        this.user = user;
        this.message = message;
        this.timeStamp = timeStamp;
        this.channel = channel;
        this.mode = mode;
    }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimeStamp() { return timeStamp; }
    public void setTimeStamp(LocalDateTime timeStamp) { this.timeStamp = timeStamp; }
    public Channel getChannel() { return channel; }
    public void setChannel(Channel channel) { this.channel = channel; }
    public Integer getMode() { return mode; }
    public void setMode(Integer mode) { this.mode = mode; }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    @Override
    public String toString() {
        return "Message{user='" + user + "', message='" + message + "', mode=" + mode + '}';
    }
}