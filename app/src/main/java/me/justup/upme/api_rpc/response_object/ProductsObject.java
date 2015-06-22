package me.justup.upme.api_rpc.response_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.justup.upme.api_rpc.response_object.sub_object.Brand;
import me.justup.upme.api_rpc.response_object.sub_object.BrandCategories;
import me.justup.upme.api_rpc.response_object.sub_object.Product;
import me.justup.upme.api_rpc.utils.Constants;

public class ProductsObject implements Serializable {

    @SerializedName(Constants.ID)
    private int id;

    @SerializedName(Constants.NAME)
    private String name;

    @SerializedName(Constants.BRAND_CATEGORIES)
    private ArrayList<BrandCategories> brandCategories;

    @SerializedName(Constants.IMAGE)
    private String image;

    @SerializedName(Constants.SHORT_DESCRIPTION)
    private String shortDescription;

    @SerializedName(Constants.FULL_DESCRIPTION)
    private String fullDescription;

    @SerializedName(Constants.CATEGORY_ID)
    private int categoryId;

    @SerializedName(Constants.BRAND_ID)
    private int brandId;

    @SerializedName(Constants.PRODUCTS)
    private List<Product> products;

    @SerializedName(Constants.BRAND)
    private Brand brand;

    @SerializedName(Constants.SHORT_DESCRIPTION_UNDERLINE)
    private String short_description;

    @SerializedName(Constants.FULL_DESCRIPTION_UNDERLINE)
    private String full_description;

    @SerializedName(Constants.CATEGORY_ID_UNDERLINE)
    private int category_id;

    @SerializedName(Constants.BRAND_ID_UNDERLINE)
    private int brand_id;

    @SerializedName(Constants.HTML)
    private String html;

    @SerializedName(Constants.DESCRIPTION)
    private String description;

    private ArrayList<ProductsObject> productsObjectArrayList = null;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<BrandCategories> getBrandCategories() {
        return brandCategories;
    }

    public String getImage() {
        return image;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getBrandId() {
        return brandId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public Brand getBrand() {
        return brand;
    }

    public String getShort_description() {
        return short_description;
    }

    public String getFull_description() {
        return full_description;
    }

    public int getCategory_id() {
        return category_id;
    }

    public int getBrand_id() {
        return brand_id;
    }

    public String getHtml() {
        return html;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<ProductsObject> getProductsObjectArrayList() {
        return productsObjectArrayList;
    }

    public void setProductsObjectArrayList(ArrayList<ProductsObject> productsObjectArrayList) {
        this.productsObjectArrayList = productsObjectArrayList;
    }

    @Override
    public String toString() {
        return "ProductsObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brandCategories=" + brandCategories +
                ", image='" + image + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", fullDescription='" + fullDescription + '\'' +
                ", categoryId=" + categoryId +
                ", brandId=" + brandId +
                ", products=" + products +
                ", brand=" + brand +
                ", short_description='" + short_description + '\'' +
                ", full_description='" + full_description + '\'' +
                ", category_id=" + category_id +
                ", brand_id=" + brand_id +
                ", html='" + html + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
