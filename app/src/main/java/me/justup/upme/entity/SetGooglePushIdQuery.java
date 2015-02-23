package me.justup.upme.entity;

import me.justup.upme.http.ApiWrapper;


public class SetGooglePushIdQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.ACCOUNT_SET_GOOGLE_PUSH_ID;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public String google_push_id;
    }

}
