package me.justup.upme.entity;

import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;

public class LoginPhoneQueryEntity extends BaseHttpQueryEntity {
    private String method = ApiWrapper.AUTH_GET_VERIFICATION;

    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {
        public String phone = "";
    }

}
