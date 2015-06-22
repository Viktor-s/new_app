package me.justup.upme.api_rpc.response_object.sub_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import me.justup.upme.api_rpc.utils.Constants;

public class BrandCategories implements Serializable {

    @SerializedName(Constants.ID)
    private int id;

    @SerializedName(Constants.NAME)
    private String name;

    @SerializedName(Constants.IMAGE)
    private String image;

    @SerializedName(Constants.SHORT_DESCRIPTION)
    private String shortDescription;

    @SerializedName(Constants.FULL_DESCRIPTION)
    private String fullDescription;

    @SerializedName(Constants.CATEGORY_ID)
    private int categoryId;

    @SerializedName(Constants.BRAND_ID_UNDERLINE)
    private int brandId;

    @SerializedName(Constants.PRODUCTS)
    private List<Product> products;

    @SerializedName(Constants.BRAND)
    private Brand brand;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    @Override
    public String toString() {
        return "BrandCategories{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", fullDescription='" + fullDescription + '\'' +
                ", categoryId=" + categoryId +
                ", brandId=" + brandId +
                ", products=" + products +
                ", brand=" + brand +
                '}';
    }
}
