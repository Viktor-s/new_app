package me.justup.upme.entity;

public class FileResultResponse extends BaseHttpResponseEntity {
    public Result result = new Result();

    public class Result extends BaseHttpParams {
        public boolean success;
    }

}
