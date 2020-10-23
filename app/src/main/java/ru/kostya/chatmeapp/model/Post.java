package ru.kostya.chatmeapp.model;

import java.security.PublicKey;

public class Post {

    private String date,postDescription,postImage,userImage,userName,status;

    public Post(String date, String postDescription, String postImage, String userImage, String userName, String status) {
        this.date = date;
        this.postDescription = postDescription;
        this.postImage = postImage;
        this.userImage = userImage;
        this.userName = userName;
        this.status = status;
    }

    public Post(){

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
