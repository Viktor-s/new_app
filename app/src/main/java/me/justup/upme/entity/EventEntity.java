package me.justup.upme.entity;

import java.io.Serializable;
import java.util.List;

public class EventEntity implements Serializable {

    public int id;
    public String name;
    public String description;
    public String type;
    public String start_datetime;
    public String end_datetime;
    public String location;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStart_datetime(String start_datetime) {
        this.start_datetime = start_datetime;
    }

    public void setEnd_datetime(String end_datetime) {
        this.end_datetime = end_datetime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "EventEntity{" +
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