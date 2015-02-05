package me.justup.upme.entity;

import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;


public class SendGPSQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.ARTICLES_GET;

    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {
        public double latitude;
        public double longitude;
    }

}
