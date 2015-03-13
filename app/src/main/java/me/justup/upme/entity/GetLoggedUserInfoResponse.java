package me.justup.upme.entity;


import java.io.Serializable;

public class GetLoggedUserInfoResponse extends BaseHttpResponseEntity {
    public Result result;

    public class Result implements Serializable {
        public String name = "";
        public int id;
        public int dateAdd = 0;
        public String login = "";
        public String jabber_id = "";
        public int network = 0;
        public String phone = "";
    }

}
