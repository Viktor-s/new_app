package me.justup.upme.entity;


public class Push {
    private int id;
    private int type;
    private int userId;
    private String userName;
    private int room;
    private String date;

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

    @Override
    public String toString() {
        return "Push{" +
                "id=" + id +
                ", type=" + type +
                ", userId=" + userId +
                ", userName=" + userName +
                ", room=" + room +
                ", date=" + date +
                '}';
    }

}
