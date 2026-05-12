package com.example.regret_it;
import java.io.Serializable;
import java.util.ArrayList;

public class Channel implements Serializable {
    private Message message;
    private String heading;
    private ArrayList<Message> responses = new ArrayList<>();
    private Integer upVote;
    private Integer downVote;
    private ArrayList<String> tags = new ArrayList<>();

    public Channel(Message message, String heading, ArrayList<Message> responses, Integer upVote, Integer downVote, ArrayList<String> tags) {
        this.message = message;
        this.heading = heading;
        this.responses = responses;
        this.upVote = upVote;
        this.downVote = downVote;
        this.tags = tags;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public ArrayList<Message> getResponses() {
        return responses;
    }

    public void setResponses(ArrayList<Message> responses) {
        this.responses = responses;
    }

    public Integer getUpVote() {
        return upVote;
    }

    public void setUpVote(Integer upVote) {
        this.upVote = upVote;
    }

    public Integer getDownVote() {
        return downVote;
    }

    public void setDownVote(Integer downVote) {
        this.downVote = downVote;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Channel{" +
                message +
                ", heading='" + heading + '\'' +
                ", responses=" + responses +
                ", upVote=" + upVote +
                ", downVote=" + downVote +
                ", tags=" + tags +
                '}';
    }
}
