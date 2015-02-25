package me.justup.upme.entity;

import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;

public class GetAccountPanelInfoQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.ACCOUNT_GET_USER_PANEL_INFO;

    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {
        public int id;
    }

}
