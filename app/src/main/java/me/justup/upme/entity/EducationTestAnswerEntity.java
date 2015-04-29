package me.justup.upme.entity;

import java.io.Serializable;

public class EducationTestAnswerEntity implements Serializable {
    private String answer_hash;
    private String answer_text;

    public String getAnswer_hash() {
        return answer_hash;
    }

    public void setAnswer_hash(String answer_hash) {
        this.answer_hash = answer_hash;
    }

    public String getAnswer_text() {
        return answer_text;
    }

    public void setAnswer_text(String answer_text) {
        this.answer_text = answer_text;
    }

    @Override
    public String toString() {
        return "EducationTestAnswerEntity{" +
                "answer_hash='" + answer_hash + '\'' +
                ", answer_text='" + answer_text + '\'' +
                '}';
    }
}
