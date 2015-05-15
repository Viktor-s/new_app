package me.justup.upme.entity;


public class PersonBriefcaseEntity {

    private int id;
    private int parentId;
    private String name;
    private String photo;
    private int status;

    public PersonBriefcaseEntity(int id, int parent_id, String name, String photo) {
        this.id = id;
        this.parentId = parent_id;
        this.name = name;
        this.photo = photo;
    }

    public PersonBriefcaseEntity() {
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", name=" + name +
                ", photo=" + photo +
                ", status=" + status +
                '}';
    }

}
