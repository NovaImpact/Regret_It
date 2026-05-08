package com.example.regret_it;
import javafx.scene.image.Image;

import javax.print.attribute.standard.Media;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private String user;
    private String message;
    private Image image;
    private Media audio;
    private Media video;
    private LocalDateTime timeStamp;
    private Channel channel;
    private Integer mode;

    public Message(String user, String message, Image image, Media audio, Media video, LocalDateTime timeStamp, Channel channel, Integer mode) {
        this.user = user;
        this.message = message;
        this.image = image;
        this.audio = audio;
        this.video = video;
        this.timeStamp = timeStamp;
        this.channel = channel;
        this.mode = mode;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Media getAudio() {
        return audio;
    }

    public void setAudio(Media audio) {
        this.audio = audio;
    }

    public Media getVideo() {
        return video;
    }

    public void setVideo(Media video) {
        this.video = video;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "Message{" +
                "user='" + user + '\'' +
                ", message='" + message + '\'' +
                ", image=" + image +
                ", audio=" + audio +
                ", video=" + video +
                ", timeStamp=" + timeStamp +
                ", channel=" + channel +
                ", mode=" + mode +
                '}';
    }
}
