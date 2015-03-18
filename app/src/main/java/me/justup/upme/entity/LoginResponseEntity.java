package me.justup.upme.entity;


public class LoginResponseEntity extends BaseHttpResponseEntity {
    public Result result;

    public class Result extends BaseHttpParams {
        public String PHONE = "";
        public String token = "";
    }

}
