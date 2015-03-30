package me.justup.upme.entity;

import java.io.Serializable;
import java.util.Map;

import me.justup.upme.http.ApiWrapper;

public class ProductsJSQuery extends BaseHttpQueryEntity {

    public int id = 123;
    public String method;
    public Params params = new Params();

    public class Params implements Serializable {
        public String product_id;
        public Map<String, String> data;

        @Override
        public String toString() {
            return "Params{" +
                    "product_id='" + product_id + '\'' +
                    ", data=" + data +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ProductsJSQuery{" +
                "id=" + id +
                ", method='" + method + '\'' +
                ", params=" + params +
                '}';
    }
}