package me.justup.upme.entity;

import java.util.ArrayList;

public class EducationGetProgramsResponse extends BaseHttpResponseEntity {
    public ArrayList<Result> result = new ArrayList<>();

    public class Result extends BaseHttpParams {
        public int id;
        public String name;


    }
}
