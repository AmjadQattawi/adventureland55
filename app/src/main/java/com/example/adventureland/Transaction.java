package com.example.adventureland;

public class Transaction {
    private String type;      // earned أو spent
    private String reason;    // مثل: "Signup Bonus", "Recharge Reward"
    private int points;       // عدد النقاط
    private String date;      // تاريخ المعاملة

    public Transaction() {
        // Required empty constructor for Firebase
    }

    public Transaction(String type, String reason, int points, String date) {
        this.type = type;
        this.reason = reason;
        this.points = points;
        this.date = date;
    }

    // Getters
    public String getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public int getPoints() {
        return points;
    }

    public String getDate() {
        return date;
    }

    // Setters
    public void setType(String type) {
        this.type = type;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
