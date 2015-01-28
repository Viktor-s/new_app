package me.justup.upme.entity;

import java.util.List;

public class GroupProductEntity {

    private int id;
    private int parentId;
    private String name;
    private String description;
    private List<ProductEntityMock> groupProduct;

    public GroupProductEntity(int id, int parentId, String name, String description, List<ProductEntityMock> groupProduct) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.description = description;
        this.groupProduct = groupProduct;
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

    public String getDescription() {
        return description;
    }

    public List<ProductEntityMock> getGroupProduct() {
        return groupProduct;
    }

    @Override
    public String toString() {
        return "GroupProductEntity{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", groupProduct=" + groupProduct +
                '}';
    }
}
