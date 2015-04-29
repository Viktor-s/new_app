package me.justup.upme.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class EducationTestEntity implements Serializable {
    private int id;
    private int module_id;
    private String name;
    private String description;
    private int pass_limit;
    private ArrayList<EducationTestQuestionEntity> questions;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getModule_id() {
        return module_id;
    }

    public void setModule_id(int module_id) {
        this.module_id = module_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPass_limit() {
        return pass_limit;
    }

    public void setPass_limit(int pass_limit) {
        this.pass_limit = pass_limit;
    }

    public ArrayList<EducationTestQuestionEntity> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<EducationTestQuestionEntity> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "EducationTestEntity{" +
                "id=" + id +
                ", module_id=" + module_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", pass_limit=" + pass_limit +
                ", questions=" + questions +
                '}';
    }
}
