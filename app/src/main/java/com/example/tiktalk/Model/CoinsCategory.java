package com.example.tiktalk.Model;

public class CoinsCategory {

    String coins;
    String amount;

    public CoinsCategory(String coins, String amount) {
        this.coins = coins;
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }
}
