package me.justup.upme.api_rpc.response_object.sub_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import me.justup.upme.api_rpc.utils.Constants;

public class Referral implements Serializable{

    @SerializedName(Constants.ID)
    private int id;

    @SerializedName(Constants.PARENT_ID)
    private int parent_id;

    @SerializedName(Constants.NAME)
    private String name;

    @SerializedName(Constants.JABBER_ID)
    private String jabber_id;

    @SerializedName(Constants.DATE_ADD)
    private long dateAdd;

    @SerializedName(Constants.LOGIN)
    private String login;

    @SerializedName(Constants.PHONE)
    private String phone;

    @SerializedName(Constants.IMG)
    private String img;

    @SerializedName(Constants.LATITUDE)
    private String latitude;

    @SerializedName(Constants.LONGITUDE)
    private String longitude;

    @SerializedName(Constants.LEVEL)
    private int level;

    @SerializedName(Constants.IN_SYSTEM)
    private String in_system;

    @SerializedName(Constants.TOTAL_SUM)
    private int total_sum;

    public int getId() {
        return id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public String getName() {
        return name;
    }

    public String getJabber_id() {
        return jabber_id;
    }

    public long getDateAdd() {
        return dateAdd;
    }

    public String getLogin() {
        return login;
    }

    public String getPhone() {
        return phone;
    }

    public String getImg() {
        return img;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getLevel() {
        return level;
    }

    public String getIn_system() {
        return in_system;
    }

    public int getTotal_sum() {
        return total_sum;
    }

    @Override
    public String toString() {
        return "Referral{" +
                "id=" + id +
                ", parent_id=" + parent_id +
                ", name='" + name + '\'' +
                ", jabber_id='" + jabber_id + '\'' +
                ", dateAdd=" + dateAdd +
                ", login='" + login + '\'' +
                ", phone='" + phone + '\'' +
                ", img='" + img + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", level=" + level +
                ", in_system='" + in_system + '\'' +
                ", total_sum=" + total_sum +
                '}';
    }
}
