package me.justup.upme.entity;


import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;


public class CalendarGetEventsQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.CALENDAR_GET_EVENTS;

    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {

        public String start;
        public String end;

        @Override
        public String toString() {
            return "Params{" +
                    "start='" + start + '\'' +
                    ", end='" + end + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CalendarGetEventsQuery{" +
                "method='" + method + '\'' +
                ", params=" + params +
                ", id=" + id +
                '}';
    }
}
