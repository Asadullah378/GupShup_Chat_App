package com.asadullahnawaz_alinaaftab.i200761_i200961;

public class UserModel {

    String email;
    String name;
    String phone_number;
    String bio;
    String gender;
    String imageUrl;

    public UserModel(){

        this.email = "";
        this.name = "";
        this.phone_number = "";
        this.bio = "";
        this.gender = "";
        this.imageUrl = "";

    }

    public UserModel(String email, String name, String phone_number, String bio, String gender, String imageUrl) {
        this.email = email;
        this.name = name;
        this.phone_number = phone_number;
        this.bio = bio;
        this.gender = gender;
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
