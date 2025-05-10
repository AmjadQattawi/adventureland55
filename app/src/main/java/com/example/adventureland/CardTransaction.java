package com.example.adventureland;

public class CardTransaction {
    private String type;    // "reward", "charge"
    private String title;   // مثل: "Recharge", "Added from Reward"
    private double amount;
    private String date;

    public CardTransaction() {
        // Required empty constructor for Firebase
    }

    public CardTransaction(String type, String title, double amount, String date) {
        this.type = type;
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
