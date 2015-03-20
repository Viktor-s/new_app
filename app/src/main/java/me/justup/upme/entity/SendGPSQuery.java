package me.justup.upme.entity;

import java.util.List;

import me.justup.upme.http.ApiWrapper;


public class SendGPSQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.ACCOUNT_ADD_USER_LOCATION;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public List<GPSEntity> data;
    }

}
