package com.example.clipmainjava.Models;

import com.google.gson.annotations.SerializedName;

public class OrderItemModel {

    @SerializedName("id")
    private Long id;

    @SerializedName("productTitle")
    private String productTitle;

    @SerializedName("productPrice")
    private String productPrice;

    @SerializedName("size")
    private String size;

    @SerializedName("quantity")
    private Integer quantity;

    @SerializedName("imageUrl")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getSize() {
        return size;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public Long getId() {
        return id;
    }
}
