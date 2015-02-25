package me.justup.upme.entity;

import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;

public class ReferalAddQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.ACCOUNT_ADD_REFERAL;
    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {
        public String name;
        public String phone;
    }
}