package com.example.adventureland;

public class Game {
    private String title;
    private int age;
    private float rating;
    private int image;
    private String description;
    private double price;
    private boolean isFavorite;

    public Game(String title, int age, float rating, int image, String description, double price, boolean isFavorite) {
        this.title = title;
        this.age = age;
        this.rating = rating;
        this.image = image;
        this.description = description;
        this.price = price;
        this.isFavorite = isFavorite;
    }

    public String getTitle() {
        return title;
    }

    public int getAge() {
        return age;
    }

    public float getRating() {
        return rating;
    }

    public int getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

}