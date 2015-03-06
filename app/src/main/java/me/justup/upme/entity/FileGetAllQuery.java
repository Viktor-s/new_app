package me.justup.upme.entity;


public class FileGetAllQuery extends BaseHttpQueryEntity {
    public String method;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
    }

}
