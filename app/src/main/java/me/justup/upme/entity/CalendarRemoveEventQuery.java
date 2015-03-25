package me.justup.upme.entity;

import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;

public class CalendarRemoveEventQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.CALENDAR_REMOVE_EVENT;

    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {

        public String id;
    }
}