package me.justup.upme.entity;

import java.io.Serializable;


public class GPSEntity implements Serializable {
    private static final long serialVersionUID = 0L;

    public String latitude;
    public String longitude;
    public long ts;


    public GPSEntity(long timeStamp, double latitude, double longitude) {
        this.ts = timeStamp;
        this.latitude = String.valueOf(latitude);
        this.longitude = String.valueOf(longitude);
    }

    @Override
    public String toString() {
        return "{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", ts=" + ts +
                "}";
    }

}
