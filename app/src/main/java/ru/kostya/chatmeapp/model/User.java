package ru.kostya.chatmeapp.model;

public class User {

    private String City,Country,Name,Profession,ProfileImage;

    public User(){}

    public User(String city, String country, String name, String profession, String profileImage) {
        City = city;
        Country = country;
        Name = name;
        Profession = profession;
        ProfileImage = profileImage;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProfession() {
        return Profession;
    }

    public void setProfession(String profession) {
        Profession = profession;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }
}
