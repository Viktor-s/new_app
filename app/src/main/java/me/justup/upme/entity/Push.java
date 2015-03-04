package me.justup.upme.entity;

import java.io.Serializable;


public class Push implements Serializable {
    private static final long serialVersionUID = 0L;

    private int id = 0;
    private int type = 0;
    private int userId = 0;
    private String userName = "";
    private int room = 0;
    private String date = "";
    private String link = "";
    private String text = "";

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

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
                ", text=" + text +
                '}';
    }

}
