package com.asadullahnawaz_alinaaftab.i200761_i200961;

public class GroupMessageModel {

    String messagetext;
    String dateTime;
    String imageUrl;
    String senderUid;
    String senderName;
    String messageID_senderSide;

    public GroupMessageModel() {
        this.messagetext = "";
        this.dateTime = "";
        this.imageUrl = "";
        this.senderUid = "";
        this.senderName = "";
        this.messageID_senderSide = "";
    }

    public GroupMessageModel(String messagetext, String dateTime, String imageUrl, String senderUid, String senderName, String messageID_senderSide) {
        this.messagetext = messagetext;
        this.dateTime = dateTime;
        this.imageUrl = imageUrl;
        this.senderUid = senderUid;
        this.senderName = senderName;
        this.messageID_senderSide = messageID_senderSide;
    }

    public String getMessagetext() {
        return messagetext;
    }

    public void setMessagetext(String messagetext) {
        this.messagetext = messagetext;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessageID_senderSide() {
        return messageID_senderSide;
    }

    public void setMessageID_senderSide(String messageID_senderSide) {
        this.messageID_senderSide = messageID_senderSide;
    }
}
