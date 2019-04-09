package com.example.tiktalk.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class SellerRating {

    @ServerTimestamp
    public Date updateDate;

    public String userId;
    public String userName;
    public String userImage;
    public String value;
    public String id;
    public String sellerId;
    public String feedback;

    public SellerRating() {
    }

    public SellerRating(String userId, String userName, String userImage, String value, String id, String sellerId, String feedback) {
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
        this.value = value;
        this.id = id;
        this.sellerId = sellerId;
        this.feedback = feedback;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
