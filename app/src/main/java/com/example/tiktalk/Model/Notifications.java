package com.example.tiktalk.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Notifications {

    @ServerTimestamp
    public Date messageTime;

    public String sender;
    public String receiver;
    public String name;
    public String message;
    public String image;
    public String status;

    public Notifications() {
    }

    public Notifications(String sender, String receiver, String name, String message, String image, String status) {
        this.sender = sender;
        this.receiver = receiver;
        this.name = name;
        this.message = message;
        this.image = image;
        this.status = status;

    }

    public Date getCallTime() {
        return messageTime;
    }

    public void setCallTime(Date callTime) {
        this.messageTime = callTime;
    }

    public String getSenderId() {
        return sender;
    }

    public void setSenderId(String senderId) {
        this.sender = senderId;
    }

    public String getReceiverId() {
        return receiver;
    }

    public void setReceiverId(String receiverId) {
        this.receiver = receiverId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
