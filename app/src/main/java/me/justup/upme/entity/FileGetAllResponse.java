package me.justup.upme.entity;

import java.util.ArrayList;


public class FileGetAllResponse extends BaseHttpResponseEntity {
    public ArrayList<Result> result = new ArrayList<>();

    public class Result extends BaseHttpParams {
        public String hash_name = "";
        public int size;
        public String origin_name = "";
    }

}
