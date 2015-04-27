package me.justup.upme.entity;

import me.justup.upme.http.ApiWrapper;

public class EducationGetProgramsQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.EDUCATION_GET_PROGRAMS;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
    }

}