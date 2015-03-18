package me.justup.upme.entity;

import java.util.ArrayList;


public class GetMailContactResponse extends BaseHttpResponseEntity {
    public ArrayList<Result> result = new ArrayList<>();

    public class Result extends BaseHttpParams {
        public int id = 0;
        public String name = "";
        public String jabber_id = "";
        public int dateAdd = 0;
        public String login = "";
        public String phone = "";
        public String img = "";
        public int parentId;

        @Override
        public String toString() {
            return "Result{" +
                    "id=" + id +
                    ", name=" + name +
                    ", jabber_id=" + jabber_id +
                    ", dateAdd=" + dateAdd +
                    ", login=" + login +
                    ", phone=" + phone +
                    ", img=" + img +
                    ", parentId=" + parentId +
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
