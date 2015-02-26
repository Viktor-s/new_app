package me.justup.upme.entity;

import java.io.Serializable;
import java.util.List;

public class CalendarGetEventsResponse extends BaseHttpResponseEntity {
    public List<Result> result;

    public class Result implements Serializable {
        public int id = 0;
        public String name = "";
        public String description = "";
        public String type = "";
        public String start_datetime = "";
        public String end_datetime = "";
        public String location = "";

        @Override
        public String toString() {
            return "Result{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", type='" + type + '\'' +
                    ", start_datetime='" + start_datetime + '\'' +
                    ", end_datetime='" + end_datetime + '\'' +
                    ", location='" + location + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "EventsCalendarResponse{" +
                "result=" + result +
                '}';
    }
}