package me.justup.upme.entity;

import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;

public class CalendarAddEventQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.CALENDAR_ADD_EVENT;

    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {

        public String name;
        public String description;
        public String type;
        public String location;
        public String start_date_time;
        public String end_date_time;

        @Override
        public String toString() {
            return "Params{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", type='" + type + '\'' +
                    ", location='" + location + '\'' +
                    ", start_date_time='" + start_date_time + '\'' +
                    ", end_date_time='" + end_date_time + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CalendarAddEventQuery{" +
                "method='" + method + '\'' +
                ", params=" + params +
                ", id=" + id +
                '}';
    }
}