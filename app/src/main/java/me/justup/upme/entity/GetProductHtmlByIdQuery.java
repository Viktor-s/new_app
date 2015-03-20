package me.justup.upme.entity;

import me.justup.upme.http.ApiWrapper;

public class GetProductHtmlByIdQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.PRODUCTS_GET_HTML_BY_ID;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public int id;
    }

}