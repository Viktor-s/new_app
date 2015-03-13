package me.justup.upme.entity;

import java.io.Serializable;
import java.util.List;

public class ProductsGetAllCategoriesResponse extends BaseHttpResponseEntity {
    public List<Result> result;

    public class Result implements Serializable {
        public int id;
        public String name;
        public List<BrandCategory> brandCategories;

        public class BrandCategory implements Serializable {
            public int id;
            public String name;
            public String image;
            public String shortDescription;
            public String fullDescription;
            public int categoryId;
            public int brandId;
            public List<Product> products;
            public Brand brand;

            public class Product implements Serializable {
                public int id;
                public String name;
                public String short_description;
                public String img;

            }

            public class Brand implements Serializable {
                public int id;
                public String name;
                public String description;
            }

        }
    }
}