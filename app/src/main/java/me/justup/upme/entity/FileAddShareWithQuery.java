package me.justup.upme.entity;

import me.justup.upme.http.ApiWrapper;


public class FileAddShareWithQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.FILE_ADD_SHARE_WITH;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public String file_hash;
        public int member_ids;
    }

}
