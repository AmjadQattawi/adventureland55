package com.example.adventureland;

public class RedeemItem {
    private String title;
    private String points;
    private int imageResource;

    public RedeemItem(String title, String points, int imageResource) {
        this.title = title;
        this.points = points;
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public String getPoints() {
        return points;
    }

    public int getImageResource() {
        return imageResource;
    }
}
