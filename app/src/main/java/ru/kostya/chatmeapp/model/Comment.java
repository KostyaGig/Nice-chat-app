package ru.kostya.chatmeapp.model;

public class Comment {
    private String comment,profileImage,userName;

    public Comment(){

    }

    public Comment(String comment, String profileImage, String userName) {
        this.comment = comment;
        this.profileImage = profileImage;
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
