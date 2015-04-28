package me.justup.upme.entity;

import me.justup.upme.http.ApiWrapper;

public class EducationGetTestsQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.EDUCATION_GET_TESTS;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public int module_id;
    }

}