package me.justup.upme.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class EducationTestQuestionEntity implements Serializable {
    private String question_text;
    private String question_hash;
    private ArrayList<EducationTestAnswerEntity> answers;

    public String getQuestion_text() {
        return question_text;
    }

    public void setQuestion_text(String question_text) {
        this.question_text = question_text;
    }

    public String getQuestion_hash() {
        return question_hash;
    }

    public void setQuestion_hash(String question_hash) {
        this.question_hash = question_hash;
    }

    public ArrayList<EducationTestAnswerEntity> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<EducationTestAnswerEntity> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "EducationTestQuestionEntity{" +
                "question_text='" + question_text + '\'' +
                ", question_hash='" + question_hash + '\'' +
                ", answers=" + answers +
                '}';
    }
}
