package me.justup.upme.entity;


import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;


public class ArticlesGetShortDescriptionQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.ARTICLES_GET_SHORT_DESCRIPTION;

    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {
        public int limit;
        public int offset;
        private String order = "ASC";
    }

}
