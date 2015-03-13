package me.justup.upme.entity;

import me.justup.upme.http.ApiWrapper;


public class FileCopySharedQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.FILE_COPY_SHARED_TO_ME;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public String file_hash;
    }

}
