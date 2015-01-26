package me.justup.upme.entity;

import java.io.Serializable;

public class LoginResponseEntity extends BaseHttpResponseEntity {
    public Result result;

    public class Result implements Serializable {
        public String PHONE = "";
        public String token = "";
    }

}
