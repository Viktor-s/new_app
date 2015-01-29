package me.justup.upme.entity;

import java.io.Serializable;
import java.util.List;

public class GetMailContactResponse extends BaseHttpResponseEntity {
    public List<Result> result;

    public class Result implements Serializable {
        public int id = 0;
        public String name = "";
        public String login = "";
        public int dateAdd = 0;
        public String phone = "";
        public String img = "";

        @Override
        public String toString() {
            return "Result{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", login='" + login + '\'' +
                    ", dateAdd=" + dateAdd +
                    ", phone='" + phone + '\'' +
                    ", img='" + img + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GetMailContactResponse{" +
                "results=" + result +
                '}';
    }
}