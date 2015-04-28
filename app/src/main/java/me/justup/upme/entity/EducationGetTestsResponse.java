package me.justup.upme.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class EducationGetTestsResponse extends BaseHttpResponseEntity {
    public ArrayList<Result> result = new ArrayList<>();

    public class Result extends BaseHttpParams {
        public int id;
        public int program_id;
        public String name;
        public String description;
        public String created_at;
        public String updated_at;
        public ArrayList<Material> materials = new ArrayList<>();
    }

    public class Material implements Serializable {
        public int id;
        public int module_id;
        public String content_type;
        public String priority_type;
        public Extradata extradata;
        public int sort_weight;
        public String created_at;
        public String updated_at;
        public String name;
        public String description;
    }

    public class Extradata implements Serializable {
        public String source;
        public String link;

    }
}
