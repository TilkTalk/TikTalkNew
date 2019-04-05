package com.example.tiktalk.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Notifications {

    @ServerTimestamp
    public Date callTime;

    public String senderId;
    public String receiverId;
    public String name;
    public String message;
    public String image;

    public Notifications() {
    }

    public Notifications(String senderId, String receiverId, String name, String message, String image) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.name = name;
        this.message = message;
        this.image = image;
    }

    public Date getCallTime() {
        return callTime;
    }

    public void setCallTime(Date callTime) {
        this.callTime = callTime;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
