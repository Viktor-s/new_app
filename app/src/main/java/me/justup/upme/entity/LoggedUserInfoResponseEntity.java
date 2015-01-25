package me.justup.upme.entity;


public class LoggedUserInfoResponseEntity extends BaseHttpResponseEntity {
    public Result result;

    public class Result {
        public String name = "";
        public int dateAdd = 0;
        public String login = "";
        public int network = 0;
        public String phone = "";
    }

}
