package me.justup.upme.entity;

import java.io.Serializable;
import java.util.Map;

public class ProductsJSQueryKey extends BaseHttpQueryEntity {

    public int id = 123;
    public String method;
    public Params params = new Params();

    public class Params implements Serializable {
        public String key;
        public Map<String, String> data;

        @Override
        public String toString() {
            return "Params{" +
                    "product_id='" + key + '\'' +
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