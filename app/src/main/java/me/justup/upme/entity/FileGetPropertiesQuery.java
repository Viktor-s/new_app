package me.justup.upme.entity;

import me.justup.upme.http.ApiWrapper;


public class FileGetPropertiesQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.FILE_GET_PROPERTIES_BY_HASH;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public String file_hash;
    }

}
