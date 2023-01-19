package com.asadullahnawaz_alinaaftab.i200761_i200961;

public class MessageModel {

    String messagetext;
    String dateTime;
    String imageUrl;
    String senderUid;
    String messageID_senderSide;
    String messageID_recieverSide;
    String seen;

    public MessageModel() {
        this.messagetext = "";
        this.dateTime = "";
        this.imageUrl = "";
        this.senderUid = "";
        this.messageID_senderSide = "";
        this.messageID_recieverSide = "";
        this.seen = "";
    }

    public MessageModel(String messagetext, String dateTime, String imageUrl, String senderUid, String messageID_senderSide, String messageID_recieverSide, String seen) {
        this.messagetext = messagetext;
        this.dateTime = dateTime;
        this.imageUrl = imageUrl;
        this.senderUid = senderUid;
        this.messageID_senderSide = messageID_senderSide;
        this.messageID_recieverSide = messageID_recieverSide;
        this.seen = seen;
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

    public String getMessageID_senderSide() {
        return messageID_senderSide;
    }

    public void setMessageID_senderSide(String messageID_senderSide) {
        this.messageID_senderSide = messageID_senderSide;
    }

    public String getMessageID_recieverSide() {
        return messageID_recieverSide;
    }

    public void setMessageID_recieverSide(String messageID_recieverSide) {
        this.messageID_recieverSide = messageID_recieverSide;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }
}
