package me.justup.upme.entity;


import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;


public class LoginPinCodeQueryEntity extends BaseHttpQueryEntity {
    private String method = ApiWrapper.AUTH_CHECK_VERIFICATION;

    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {
        public String phone = "";
        public String code = "";
    }

}
