package me.justup.upme.entity;

import java.io.Serializable;

public class EducationMaterialEntity implements Serializable {
    private int id;
    private int module_id;
    private String content_type;
    private String priority_type;
    private int sort_weight;
    private String created_at;
    private String updated_at;
    private String extraSource;
    private String extraLink;
    private String name;
    private String description;

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

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getPriority_type() {
        return priority_type;
    }

    public void setPriority_type(String priority_type) {
        this.priority_type = priority_type;
    }

    public int getSort_weight() {
        return sort_weight;
    }

    public void setSort_weight(int sort_weight) {
        this.sort_weight = sort_weight;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getExtraSource() {
        return extraSource;
    }

    public void setExtraSource(String extraSource) {
        this.extraSource = extraSource;
    }

    public String getExtraLink() {
        return extraLink;
    }

    public void setExtraLink(String extraLink) {
        this.extraLink = extraLink;
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

    @Override
    public String toString() {
        return "EducationMaterialEntity{" +
                "id=" + id +
                ", module_id=" + module_id +
                ", content_type=" + content_type +
                ", priority_type=" + priority_type +
                ", sort_weight=" + sort_weight +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", extraSource=" + extraSource +
                ", extraLink=" + extraLink +
                ", name=" + name +
                ", description=" + description +
                '}';
    }

}
