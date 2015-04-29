package me.justup.upme.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class EducationGetTestsResponse extends BaseHttpResponseEntity {
    public ArrayList<Result> result;

    public class Result extends BaseHttpParams {
        public int id;
        public int module_id;
        public String name;
        public String description;
        public int pass_limit;
        public ArrayList<Question> questions = new ArrayList<>();
    }

    public class Question implements Serializable {
        public String question_text;
        public String question_hash;
        public ArrayList<Answer> answers;
    }

    public class Answer implements Serializable {
        public String answer_hash;
        public String answer_text;
    }
}
