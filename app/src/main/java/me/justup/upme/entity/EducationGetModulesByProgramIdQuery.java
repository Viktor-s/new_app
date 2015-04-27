package me.justup.upme.entity;

import me.justup.upme.http.ApiWrapper;

public class EducationGetModulesByProgramIdQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.EDUCATION_GET_MODELES_BY_PROGRAM_ID;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public int program_id;
    }

}