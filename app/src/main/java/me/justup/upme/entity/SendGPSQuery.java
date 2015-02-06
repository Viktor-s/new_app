package me.justup.upme.entity;

import java.util.List;

import me.justup.upme.http.ApiWrapper;


public class SendGPSQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.ARTICLES_GET;

    public List<GPSEntity> params;
    public int id = 123;

}
