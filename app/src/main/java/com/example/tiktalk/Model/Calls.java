package com.example.tiktalk.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Calls {

    @ServerTimestamp
    public Date callTime;

    public String buyerId;
    public String buyerImage;
    public String buyerName;
    public String callDuration;
    public String dollerEarned;
    public String id;
    public String sellerId;
    public String sellerImage;
    String sellerName;
    public String status;
    public String coinsUsed;

    public Calls() {
    }

    public Calls(String buyerId, String buyerImage, String buyerName, String callDuration, String dollerEarned, String id, String sellerId, String sellerImage, String sellerName, String status, String coinsUsed) {
        this.buyerId = buyerId;
        this.buyerImage = buyerImage;
        this.buyerName = buyerName;
        this.callDuration = callDuration;
        this.dollerEarned = dollerEarned;
        this.id = id;
        this.sellerId = sellerId;
        this.sellerImage = sellerImage;
        this.sellerName = sellerName;
        this.status = status;
        this.coinsUsed = coinsUsed;
    }

    public Date getCallTime() {
        return callTime;
    }

    public void setCallTime(Date callTime) {
        this.callTime = callTime;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerImage() {
        return buyerImage;
    }

    public void setBuyerImage(String buyerImage) {
        this.buyerImage = buyerImage;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getDollerEarned() {
        return dollerEarned;
    }

    public void setDollerEarned(String dollerEarned) {
        this.dollerEarned = dollerEarned;
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

    public String getSellerImage() {
        return sellerImage;
    }

    public void setSellerImage(String sellerImage) {
        this.sellerImage = sellerImage;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCoinsUsed() {
        return coinsUsed;
    }

    public void setCoinsUsed(String coinsUsed) {
        this.coinsUsed = coinsUsed;
    }
}
