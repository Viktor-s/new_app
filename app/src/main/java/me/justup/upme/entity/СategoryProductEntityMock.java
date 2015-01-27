package me.justup.upme.entity;

import java.util.List;

public class СategoryProductEntityMock {

    private int id;
    private String name;
    private List<GroupProductEntity> categoryProduct;

    public СategoryProductEntityMock(int id, String name, List<GroupProductEntity> categoryProduct) {
        this.id = id;
        this.name = name;
        this.categoryProduct = categoryProduct;
    }

    @Override
    public String toString() {
        return "СategoryProductEntityMock{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryProduct=" + categoryProduct +
                '}';
    }
}
