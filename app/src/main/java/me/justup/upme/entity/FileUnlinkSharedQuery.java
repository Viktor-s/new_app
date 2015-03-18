package me.justup.upme.entity;

import me.justup.upme.http.ApiWrapper;


public class FileUnlinkSharedQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.FILE_UNLINK_SHARED_FILE;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public String file_hash;
    }

}
