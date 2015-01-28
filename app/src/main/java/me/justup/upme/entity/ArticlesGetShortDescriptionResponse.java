package me.justup.upme.entity;


import java.io.Serializable;

public class ArticlesGetShortDescriptionResponse extends BaseHttpResponseEntity {
    public Result result;

    public class Result implements Serializable {
        public int testId;
        public String test;
    }

}
