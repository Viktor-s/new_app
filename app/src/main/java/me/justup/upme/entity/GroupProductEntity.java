package me.justup.upme.entity;

import java.util.List;

public class GroupProductEntity {

    private int id;
    private String name;
    private List<ProductEntityMock> groupProduct;

    public GroupProductEntity(int id, String name, List<ProductEntityMock> groupProduct) {
        this.id = id;
        this.name = name;
        this.groupProduct = groupProduct;
    }

    @Override
    public String toString() {
        return "GroupProductEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", groupProduct=" + groupProduct +
                '}';
    }
}
