package me.justup.upme.entity;

import java.util.List;

public class ProductCategoryEntity {

    private int id;
    private String name;
    private List<ProductsCategoryBrandEntity> brandList;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductsCategoryBrandEntity> getBrandList() {
        return brandList;
    }

    public void setBrandList(List<ProductsCategoryBrandEntity> brandList) {
        this.brandList = brandList;
    }
}
