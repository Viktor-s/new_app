package me.justup.upme.entity;

import java.io.Serializable;


public class GPSEntity implements Serializable {
    private static final long serialVersionUID = 0L;

    public long date;
    public double latitude;
    public double longitude;

    public GPSEntity(long date, double latitude, double longitude) {
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "GPSEntity{" +
                "date=" + date +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

}
