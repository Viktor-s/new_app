package me.justup.upme.entity;

import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;

public class CommentsArticleFullQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.ARTICLE_FULL_GET_COMMENTS;
    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {
        public int article_id;
        public int limit;
        public int offset;
    }
}




