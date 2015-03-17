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
        public int owner_id = 0;
        public String start_datetime = "";
        public String end_datetime = "";
        public String location = "";
        public List<Integer> shared_with;

        @Override
        public String toString() {
            return "Result{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", type='" + type + '\'' +
                    ", owner_id=" + owner_id +
                    ", start_datetime='" + start_datetime + '\'' +
                    ", end_datetime='" + end_datetime + '\'' +
                    ", location='" + location + '\'' +
                    ", shared_with=" + shared_with +
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