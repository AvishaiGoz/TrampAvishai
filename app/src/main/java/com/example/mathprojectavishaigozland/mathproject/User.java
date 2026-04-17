package com.example.mathprojectavishaigozland.mathproject;

import android.graphics.Bitmap;
import android.net.Uri;

public class User {
    Uri uri;
    private long id;
    private String userName;
    private int rating;
    private Bitmap bitmap;
    private int score;


    public User(Long id, String userName, int rating, Bitmap bitmap, int score) {
        this.id = id;
        this.userName = userName;
        this.rating = rating;
        this.bitmap = bitmap;
        this.score = score;
    }

    public User(String name) {
        this.userName = name;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
