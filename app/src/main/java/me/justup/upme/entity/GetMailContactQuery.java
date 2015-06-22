package me.justup.upme.entity;

import me.justup.upme.JustUpApplication;
import me.justup.upme.http.ApiWrapper;

public class GetMailContactQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.ACCOUNT_GET_REFERRALS_BY_ID;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public int user_id = JustUpApplication.getApplication().getAppPreferences().getUserId();
    }

}