package me.justup.upme.weekview;

import java.util.Calendar;

public class WeekViewEvent {
    private long mId;
    private Calendar mStartTime;
    private Calendar mEndTime;
    private int mDayOfWeek = -1;
    private String mName;
    private int mColor;

    private String mDescription;
    private String mType;
    private int mOwnerId;
    private String mLocation;
    private String mSharedWith;

    public WeekViewEvent(){

    }

    /**
     * Initializes the event for week view.
     * @param name Name of the event.
     * @param startYear Year when the event starts.
     * @param startMonth Month when the event starts.
     * @param startDay Day when the event starts.
     * @param startHour Hour (in 24-hour format) when the event starts.
     * @param startMinute Minute when the event starts.
     * @param endYear Year when the event ends.
     * @param endMonth Month when the event ends.
     * @param endDay Day when the event ends.
     * @param endHour Hour (in 24-hour format) when the event ends.
     * @param endMinute Minute when the event ends.
     */
    public WeekViewEvent(long id, String name, int startYear, int startMonth, int startDay, int startHour, int startMinute, int endYear, int endMonth, int endDay, int endHour, int endMinute) {
        this.mId = id;

        this.mStartTime = Calendar.getInstance();
        this.mStartTime.set(Calendar.YEAR, startYear);
        this.mStartTime.set(Calendar.MONTH, startMonth-1);
        this.mStartTime.set(Calendar.DAY_OF_MONTH, startDay);
        this.mStartTime.set(Calendar.HOUR_OF_DAY, startHour);
        this.mStartTime.set(Calendar.MINUTE, startMinute);

        this.mEndTime = Calendar.getInstance();
        this.mEndTime.set(Calendar.YEAR, endYear);
        this.mEndTime.set(Calendar.MONTH, endMonth-1);
        this.mEndTime.set(Calendar.DAY_OF_MONTH, endDay);
        this.mEndTime.set(Calendar.HOUR_OF_DAY, endHour);
        this.mEndTime.set(Calendar.MINUTE, endMinute);

        this.mName = name;
    }

    /**
     * Initializes the event for week view.
     * @param name Name of the event.
     * @param startTime The time when the event starts.
     * @param endTime The time when the event ends.
     */
    public WeekViewEvent(long id, String name, Calendar startTime, Calendar endTime) {
        this.mId = id;
        this.mName = name;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
    }

    public WeekViewEvent(long id, String name, String description, String type, int ownerId,
                         Calendar startTime, Calendar endTime, String location, String sharedWith) {
        this.mId = id;
        this.mName = name;
        this.mDescription = description;
        this.mType = type;
        this.mOwnerId = ownerId;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mLocation = location;
        this.mSharedWith = sharedWith;
    }

    public WeekViewEvent(long id, String name, int dayOfWeek, int startHour, int startMinute, int endHour, int endMinute) {
        this.mId = id;

        this.mStartTime = Calendar.getInstance();
        this.mStartTime.set(Calendar.YEAR, 2014);
        this.mStartTime.set(Calendar.MONTH, 11);
        this.mStartTime.set(Calendar.DAY_OF_MONTH, 1);
        this.mStartTime.set(Calendar.HOUR_OF_DAY, startHour);
        this.mStartTime.set(Calendar.MINUTE, startMinute);

        this.mEndTime = Calendar.getInstance();
        this.mEndTime.set(Calendar.YEAR, 2014);
        this.mEndTime.set(Calendar.MONTH, 11);
        this.mEndTime.set(Calendar.DAY_OF_MONTH, 1);
        this.mEndTime.set(Calendar.HOUR_OF_DAY, endHour);
        this.mEndTime.set(Calendar.MINUTE, endMinute);

        this.mName = name;
        this.mDayOfWeek = dayOfWeek;
    }


    public Calendar getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Calendar startTime) {
        this.mStartTime = startTime;
    }

    public Calendar getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Calendar endTime) {
        this.mEndTime = endTime;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public int getDayOfWeek() {
        return mDayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.mDayOfWeek = dayOfWeek;
    }

    public long getmId() {
        return mId;
    }


    public String getDescription() {
        return mDescription;
    }

    public String getType() {
        return mType;
    }

    public int getOwnerId() {
        return mOwnerId;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getmSharedWith() {
        return mSharedWith;
    }
}