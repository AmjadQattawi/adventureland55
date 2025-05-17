package com.example.adventureland;

public class CardTransaction {
    private String type;    // "reward", "charge", "redeem"
    private String title;   // مثل: "Recharge", "Added from Reward"
    private double amount;
    private String date;
    private String cardNumber; // ⬅️ الجديد

    public CardTransaction() {
        // Required empty constructor for Firebase
    }

    public CardTransaction(String type, String title, double amount, String date) {
        this.type = type;
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    public CardTransaction(String type, String title, double amount, String date, String cardNumber) {
        this.type = type;
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.cardNumber = cardNumber;
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

    public String getCardNumber() {
        return cardNumber;
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

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
