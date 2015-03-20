package me.justup.upme.entity;

public class GetProductHtmlByIdResponse extends BaseHttpResponseEntity {
    public Result result = new Result();

    public class Result extends BaseHttpParams {
        public int id;
        public String html;
    }

}