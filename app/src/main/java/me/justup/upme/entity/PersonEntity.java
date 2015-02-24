package me.justup.upme.entity;


public class PersonEntity {

    private int id;
    private int parentId;
    private String name;
    private String photo;

    public PersonEntity(int id, int parent_id, String name, String photo) {
        this.id = id;
        this.parentId = parent_id;
        this.name = name;
        this.photo = photo;
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

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", parent_id=" + parentId +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
