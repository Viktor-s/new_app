package me.justup.upme.api_rpc.response_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import me.justup.upme.api_rpc.utils.Constants;

public class PushObject implements Serializable{

    @SerializedName(Constants.USER_ID)
    private int user_id;

    @SerializedName(Constants.GOOGLE_PUSH_ID)
    private int google_push_id;

    @SerializedName(Constants.ID)
    private int id;

    @SerializedName(Constants.TYPE)
    private int type;

    @SerializedName(Constants.USERID)
    private int userId;

    @SerializedName(Constants.USER_NAME)
    private String userName;

    @SerializedName(Constants.ROOM)
    private String room;

    @SerializedName(Constants.DATE)
    private String date;

    @SerializedName(Constants.LINK)
    private String link;

    @SerializedName(Constants.JABBERID)
    private String jabberId;

    @SerializedName(Constants.FILENAME)
    private String fileName;

    @SerializedName(Constants.FORM_ID)
    private String formId;

    @SerializedName(Constants.PUSH_DESCRIPTION)
    private String pushDescription;

    public int getUser_id() {
        return user_id;
    }

    public int getGoogle_push_id() {
        return google_push_id;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getRoom() {
        return room;
    }

    public String getDate() {
        return date;
    }

    public String getLink() {
        return link;
    }

    public String getJabberId() {
        return jabberId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFormId() {
        return formId;
    }

    public String getPushDescription() {
        return pushDescription;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setGoogle_push_id(int google_push_id) {
        this.google_push_id = google_push_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setJabberId(String jabberId) {
        this.jabberId = jabberId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public void setPushDescription(String pushDescription) {
        this.pushDescription = pushDescription;
    }

    @Override
    public String toString() {
        return "PushObject{" +
                "user_id=" + user_id +
                ", google_push_id=" + google_push_id +
                ", id=" + id +
                ", type=" + type +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", room='" + room + '\'' +
                ", date='" + date + '\'' +
                ", link='" + link + '\'' +
                ", jabberId='" + jabberId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", formId='" + formId + '\'' +
                ", pushDescription='" + pushDescription + '\'' +
                '}';
    }
}
