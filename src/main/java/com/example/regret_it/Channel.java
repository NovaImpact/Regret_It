package com.example.regret_it;

import java.io.Serializable;
import java.util.ArrayList;

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    private Message message;
    private String heading;
    private ArrayList<Message> responses = new ArrayList<>();
    private int upVote;
    private int downVote;
    private ArrayList<String> tags = new ArrayList<>();
    private String postId;

    public Channel(Message message, String heading, ArrayList<Message> responses, int upVote, int downVote, ArrayList<String> tags, String postId) {
        this.message = message;
        this.heading = heading;
        this.responses = responses != null ? responses : new ArrayList<>();
        this.upVote = upVote;
        this.downVote = downVote;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.postId = postId;
    }

    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }
    public String getHeading() { return heading; }
    public void setHeading(String heading) { this.heading = heading; }
    public ArrayList<Message> getResponses() { return responses; }
    public void setResponses(ArrayList<Message> responses) { this.responses = responses; }
    public int getUpVote() { return upVote; }
    public void setUpVote(int upVote) { this.upVote = upVote; }
    public int getDownVote() { return downVote; }
    public void setDownVote(int downVote) { this.downVote = downVote; }
    public ArrayList<String> getTags() { return tags; }
    public void setTags(ArrayList<String> tags) { this.tags = tags; }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public int getScore() { return upVote - downVote; }

    @Override
    public String toString() {
        return "Channel{heading='" + heading + "', upVote=" + upVote + ", downVote=" + downVote + '}';
    }
}