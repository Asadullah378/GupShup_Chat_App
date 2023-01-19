package com.asadullahnawaz_alinaaftab.i200761_i200961;

public class ChatModel {

    String name;
    String uid;
    String imageUrl;
    String lastMessage;
    String lastTime;
    boolean you;
    long unseen;

    public ChatModel(String name, String uid, String imageUrl, String lastMessage, String lastTime) {
        this.name = name;
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.lastMessage = lastMessage;
        this.lastTime = lastTime;
        unseen = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }
}
