package com.example.hogwartsbattle.Model;

public class User {

    private String userName;
    private String userId;
    private String userEmail;
    private boolean host, firstTime;

    private int wins, loses;

    public User() {
    }

    public User(String userName, String userId, String userEmail) {
        this.userName = userName;
        this.userId = userId;
        this.userEmail = userEmail;
    }

    public User(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }

    public void updateWins(){
        this.wins = wins+1;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void updateLoses(){
        this.loses = loses+1;
    }
    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }
}
