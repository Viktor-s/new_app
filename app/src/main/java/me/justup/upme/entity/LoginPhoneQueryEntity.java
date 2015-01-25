package me.justup.upme.entity;


import me.justup.upme.http.ApiWrapper;


public class LoginPhoneQueryEntity extends BaseHttpQueryEntity {
    private String method = ApiWrapper.AUTH_GET_VERIFICATION;

    public Params params = new Params();
    public int id = 123;

    public class Params {
        public String phone = "";
    }

}
