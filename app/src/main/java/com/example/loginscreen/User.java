package com.example.loginscreen;

public class User {
    private String userName;
    private String userHobby;
    private String userDesc;
    private String userImage;
    private String userThumbnail;
    private String userToken;

    //needed when database read
    public User() {
    }

    public User(String userName, String userHobby, String userDesc, String userImage, String userThumbnail, String userToken) {
        this.userName = userName;
        this.userHobby = userHobby;
        this.userDesc = userDesc;
        this.userImage = userImage;
        this.userThumbnail = userThumbnail;
        this.userToken = userToken;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserHobby() {
        return userHobby;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getUserThumbnail() {
        return userThumbnail;
    }
}
