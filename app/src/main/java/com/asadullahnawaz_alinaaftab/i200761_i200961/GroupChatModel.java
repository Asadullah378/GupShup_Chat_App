package com.asadullahnawaz_alinaaftab.i200761_i200961;

import java.util.ArrayList;

public class GroupChatModel {

    String groupName;
    ArrayList<ContactModel> contacts;
    String grpId;
    String grp_lastMessage;
    String grp_lastTime;
    String grp_lastSender;
    Boolean you;


    public GroupChatModel() {
        this.groupName= "";
        contacts = new ArrayList<ContactModel>();
        this.grpId= "";
        this.grp_lastMessage= "";
        this.grp_lastTime= "";
    }

    public GroupChatModel(String groupName, ArrayList<ContactModel> contacts) {
        this.groupName = groupName;
        this.contacts = contacts;
        this.grpId= "";
        this.grp_lastMessage= "";
        this.grp_lastTime= "";
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<ContactModel> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<ContactModel> contacts) {
        this.contacts = contacts;
    }

    public String getGrpId() {
        return grpId;
    }

    public void setGrpId(String grpId) {
        this.grpId = grpId;
    }

    public String getGrp_lastMessage() {
        return grp_lastMessage;
    }

    public void setGrp_lastMessage(String grp_lastMessage) {
        this.grp_lastMessage = grp_lastMessage;
    }

    public String getGrp_lastTime() {
        return grp_lastTime;
    }

    public void setGrp_lastTime(String grp_lastTime) {
        this.grp_lastTime = grp_lastTime;
    }
}
