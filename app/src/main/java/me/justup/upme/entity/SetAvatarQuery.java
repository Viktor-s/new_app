package me.justup.upme.entity;

import me.justup.upme.http.ApiWrapper;


public class SetAvatarQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.ACCOUNT_SET_AVATAR_FILE;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public String file_hash;
        private final boolean delete_source = true;
    }

}
