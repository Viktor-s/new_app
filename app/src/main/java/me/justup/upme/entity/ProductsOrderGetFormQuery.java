package me.justup.upme.entity;

import me.justup.upme.http.ApiWrapper;


public class ProductsOrderGetFormQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.PRODUCTS_ORDER_GET_FORM;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public String key;
    }

}
