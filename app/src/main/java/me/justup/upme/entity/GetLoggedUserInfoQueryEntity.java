package me.justup.upme.entity;


import me.justup.upme.http.ApiWrapper;


public class GetLoggedUserInfoQueryEntity extends BaseHttpQueryEntity {
    private String method = ApiWrapper.AUTH_GET_LOGGED_USER_INFO;

    public Params params = new Params();
    public int id = 123;

    public class Params {
    }

}
