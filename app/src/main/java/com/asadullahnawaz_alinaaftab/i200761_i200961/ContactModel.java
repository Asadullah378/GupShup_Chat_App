package com.asadullahnawaz_alinaaftab.i200761_i200961;

public class ContactModel {
    String uid;
    String name;
    String email;
    String bio;
    String gender;
    String url;
    String phone_number;

    public ContactModel() {
        this.uid = "";
        this.name = "";
        this.email = "";
        this.bio = "";
        this.gender = "";
        this.url = "";
        this.phone_number = "";
    }

    public ContactModel(String uid, String name,String phone_number, String email, String bio, String gender, String url) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.gender = gender;
        this.url = url;
        this.phone_number = phone_number;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
