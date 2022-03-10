package com.atillaeren.socialapp.models;

public class ModelChat {
    String message, receiver, sender, timestamp, type;
    boolean seenCheck;

    public ModelChat(){

    }

    public ModelChat(String message, String receiver, String sender, String timestamp, String type, boolean seenCheck) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.type = type;
        this.seenCheck = seenCheck;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSeenCheck() {
        return seenCheck;
    }

    public void setSeenCheck(boolean seenCheck) {
        this.seenCheck = seenCheck;
    }
}
