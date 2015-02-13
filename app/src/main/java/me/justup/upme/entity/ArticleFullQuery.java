package me.justup.upme.entity;

import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;

public class ArticleFullQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.ARTICLE_GET_FULL_DESCRIPTION;
    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {
        public int id;
    }
}
