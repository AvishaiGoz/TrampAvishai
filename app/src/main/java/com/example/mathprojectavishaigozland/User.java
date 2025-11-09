package com.example.mathprojectavishaigozland;

public class User {
    private String userName;
    private int score;


    public User(String name){
        this.userName = name;
        this.score = 0;
    }

    public String getUserName(){
        return userName;
    }

    public void setUserName(String name){
        this.userName = name;
    }

    public int getScore(){
        return score;
    }

    public void setScore(int score){
        this.score = score;
    }
}
