package com.example.tiktalk.Model;

import java.io.Serializable;

public class User  implements Serializable {

    public String username;
    public String email;
    public String password;
    public String IsActive;
    public String Type;
    public String id;
    public String imageUrl;
    public String isOnline;
    public String rating;
    public String ratePerMin;
    public String coinPerMin;
    public String coins;

    public User() {
    }

    public User(String username, String email, String password, String IsActive, String Type, String id, String imageUrl, String isOnline, String rating, String ratePerMin, String coinPerMin) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.IsActive = IsActive;
        this.Type = Type;
        this.id = id;
        this.imageUrl = imageUrl;
        this.isOnline = isOnline;
        this.rating = rating;
        this.ratePerMin = ratePerMin;
        this.coinPerMin = coinPerMin;
    }

    public User(String username, String email, String password, String isActive, String type, String id, String imageUrl, String isOnline, String coins) {
        this.username = username;
        this.email = email;
        this.password = password;
        IsActive = isActive;
        Type = type;
        this.id = id;
        this.imageUrl = imageUrl;
        this.isOnline = isOnline;
        this.coins = coins;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getCoinPerMin() {
        return coinPerMin;
    }

    public void setCoinPerMin(String coinPerMin) {
        this.coinPerMin = coinPerMin;
    }

    public String getRatePerMin() {
        return ratePerMin;
    }

    public void setRatePerMin(String ratePerMin) {
        this.ratePerMin = ratePerMin;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String IsActive) {
        this.IsActive = IsActive;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
