package me.justup.upme.entity;

import java.io.Serializable;

import me.justup.upme.http.ApiWrapper;

public class ProductsGetAllCategoriesQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.PRODUCTS_GET_ALL_CATEGORIES;
    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {

    }

}