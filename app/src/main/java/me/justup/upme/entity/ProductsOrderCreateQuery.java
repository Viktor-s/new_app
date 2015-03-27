package me.justup.upme.entity;

import java.io.Serializable;
import java.util.Map;

import me.justup.upme.http.ApiWrapper;

public class ProductsOrderCreateQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.PRODUCTS_ORDER_CREATE;

    public Params params = new Params();
    public int id = 123;

    public class Params implements Serializable {
        public long product_id;
        public Map<String, String> data;

    }

}