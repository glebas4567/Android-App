// Review.java
package com.example.googlemapsjava;

public class Review {
    private String text;
    private float rating;
    private String username;
    private String objectId; // Идентификатор объекта

    public Review(String text, float rating, String username, String objectId) {
        this.text = text;
        this.rating = rating;
        this.username = username;
        this.objectId = objectId;
    }

    public String getText() {
        return text;
    }

    public float getRating() {
        return rating;
    }

    public String getUsername() {
        return username;
    }

    public String getObjectId() {
        return objectId;
    }
}

