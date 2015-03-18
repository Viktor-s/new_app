package me.justup.upme.entity;

import java.util.ArrayList;


public class FileGetShareWithResponse extends BaseHttpResponseEntity {
    public ArrayList<Result> result = new ArrayList<>();

    public class Result extends BaseHttpParams {
        public int id = 0;
        public String name = "";
    }

}
