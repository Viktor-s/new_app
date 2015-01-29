package me.justup.upme.entity;

import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;

public class GetMailContactQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.ACCOUNT_GET_PEOPLE_NETWORK;

    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {
    }

}