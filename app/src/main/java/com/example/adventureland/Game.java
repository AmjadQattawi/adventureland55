package com.example.adventureland;

public class Game {
    private String title;
    private int age;
    private float rating;
    private int image;
    private String description;
    private double price;

    // منشئ بدون معلمات مطلوب لـ Firebase
    public Game() {
    }

    // منشئ بكامل المعطيات
    public Game(String title, int age, float rating, int image, String description, double price) {
        this.title = title;
        this.age = age;
        this.rating = rating;
        this.image = image;
        this.description = description;
        this.price = price;
    }

    // Getters
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

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // ✅ لتسهيل الفلترة على أساس العمر (بديل getMinAge)
    public int getMinAge() {
        return age;
    }
}
