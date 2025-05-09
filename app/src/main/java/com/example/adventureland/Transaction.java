package com.example.adventureland;

public class Transaction {
    private String type;
    private int points;
    private String date;


    public Transaction() {}


    public Transaction(String type, int points, String date) {
        this.type = type;
        this.points = points;
        this.date = date;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
