package com.cnidaria.ovbhafinal;

import com.google.firebase.firestore.PropertyName;

import com.google.firebase.firestore.PropertyName;
import java.util.Date;

public class Message {
    private String username;
    private String messageText;
    private String chatroomId;
    private Date timestamp;

    // Required default constructor for Firestore
    public Message() {
    }

    public Message(String username, String messageText, String chatroomId, Date timestamp) {
        this.username = username;
        this.messageText = messageText;
        this.chatroomId = chatroomId;
        this.timestamp = timestamp;
    }

    @PropertyName("username")
    public String getUsername() {
        return username;
    }

    @PropertyName("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @PropertyName("messageText")
    public String getMessageText() {
        return messageText;
    }

    @PropertyName("messageText")
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    @PropertyName("chatroomId")
    public String getChatroomId() {
        return chatroomId;
    }

    @PropertyName("chatroomId")
    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    @PropertyName("timestamp")
    public Date getTimestamp() {
        return timestamp;
    }

    @PropertyName("timestamp")
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

