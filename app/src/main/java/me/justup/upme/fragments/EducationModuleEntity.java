package me.justup.upme.fragments;

import java.io.Serializable;
import java.util.ArrayList;

public class EducationModuleEntity implements Serializable {
    private int id;
    private int program_id;
    private String name;
    private String description;
    private String created_at;
    private String updated_at;
    private ArrayList<EducationMaterialEntity> materials = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProgram_id() {
        return program_id;
    }

    public void setProgram_id(int program_id) {
        this.program_id = program_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<EducationMaterialEntity> getMaterials() {
        return materials;
    }

    public void setMaterials(ArrayList<EducationMaterialEntity> materials) {
        this.materials = materials;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
