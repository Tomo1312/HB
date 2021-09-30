package com.example.hogwartsbattle.Model;

public class User {

    private String userName;
    private String userId;
    private String userEmail;
    private boolean host;

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
}
