package ru.kostya.chatmeapp.model;

public class Friend {
    private String profession,profileImage,userName;

    public Friend(){}

    public Friend(String profession, String profileImage, String userName) {
        this.profession = profession;
        this.profileImage = profileImage;
        this.userName = userName;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
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
