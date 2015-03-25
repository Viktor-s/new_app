package me.justup.upme.entity;

import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;

public class CalendarUpdateEventQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.CALENDAR_UPDATE_EVENT;

    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {
        public int id;
        public String name;
        public String description;
        public String type;
        public String location;
        public String start;
        public String end;
        public String shared_with;
    }
}