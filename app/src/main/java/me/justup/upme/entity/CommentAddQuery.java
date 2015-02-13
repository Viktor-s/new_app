package me.justup.upme.entity;

import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;

public class CommentAddQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.ARTICLE_ADD_COMMENT;
    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {
        public String content;
        public int article_id;
    }
}