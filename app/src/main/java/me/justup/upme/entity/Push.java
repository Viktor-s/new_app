package me.justup.upme.entity;

import java.io.Serializable;


public class Push implements Serializable {
    private static final long serialVersionUID = 0L;

    private int id = 0;
    private int type = 0;
    private int userId = 0;
    private String userName = "";
    private String room = "";
    private String date = "";
    private String link = "";
    private String jabberId = "";
    private String fileName = "";
    private String formId = "";
    private String pushDescription = "";

    public String getPushDescription() {
        return pushDescription;
    }

    public void setPushDescription(String pushDescription) {
        this.pushDescription = pushDescription;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getJabberId() {
        return jabberId;
    }

    public void setJabberId(String jabberId) {
        this.jabberId = jabberId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "Push{" +
                "id=" + id +
                ", type=" + type +
                ", userId=" + userId +
                ", userName=" + userName +
                ", room=" + room +
                ", date=" + date +
                ", link=" + link +
                ", jabberId=" + jabberId +
                ", fileName=" + fileName +
                ", formId=" + formId +
                ", pushDescription=" + pushDescription +
                '}';
    }

}
