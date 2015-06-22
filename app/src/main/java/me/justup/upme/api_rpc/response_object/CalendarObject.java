package me.justup.upme.api_rpc.response_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.justup.upme.api_rpc.utils.Constants;

public class CalendarObject implements Serializable{

    @SerializedName(Constants.ID)
    private int id;

    @SerializedName(Constants.NAME)
    private String name;

    @SerializedName(Constants.DESCRIPTION)
    private String description;

    @SerializedName(Constants.TYPE)
    private String type;

    @SerializedName(Constants.OWNER_ID)
    private int owner_id;

    @SerializedName(Constants.START_DATETIME)
    private long start_datetime;

    @SerializedName(Constants.END_DATETIME)
    private long end_datetime;

    @SerializedName(Constants.LOCATION)
    private String location;

    @SerializedName(Constants.SHARE_WITH)
    private List<Integer> shared_with;

    private ArrayList<CalendarObject> calendarObjectArrayList;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public long getStart_datetime() {
        return start_datetime;
    }

    public long getEnd_datetime() {
        return end_datetime;
    }

    public String getLocation() {
        return location;
    }

    public List<Integer> getShared_with() {
        return shared_with;
    }

    public ArrayList<CalendarObject> getCalendarObjectArrayList() {
        return calendarObjectArrayList;
    }

    public void setCalendarObjectArrayList(ArrayList<CalendarObject> calendarObjectArrayList) {
        this.calendarObjectArrayList = calendarObjectArrayList;
    }

    @Override
    public String toString() {
        return "CalendarObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", owner_id=" + owner_id +
                ", start_datetime=" + start_datetime +
                ", end_datetime=" + end_datetime +
                ", location='" + location + '\'' +
                ", shared_with=" + shared_with +
                '}';
    }
}
