package com.example.clipmainjava.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductModel {

    @SerializedName("id")
    private Long id;
    @SerializedName("title")
    private String title;

    @SerializedName("price")
    private Double price;

    @SerializedName("description")
    private String description;

    @SerializedName("images")
    private List<ImageModel> images;

    @SerializedName("sizes")
    private List<SizeModel> sizes;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public Double getPrice() {
        return price;
    }

    public List<SizeModel> getSizes() {
        return sizes;
    }

    public String getDescription() {
        return description;
    }

    public List<ImageModel> getImages() {
        return images;
    }

    public String getFirstImageURL() {
        if (!images.isEmpty() && images != null) {
            return images.get(0).getImageURL();
        }
        return null;
    }

    public String getSizesString() {
        if (sizes == null || sizes.isEmpty()) {
            return "нет размеров";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sizes.size(); i++) {
            sb.append(sizes.get(i).getName());
                if (i < sizes.size() - 1) {
                    sb.append(", ");
                }
        }
        return sb.toString();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImages(List<ImageModel> images) {
        this.images = images;
    }

    public void setSizes(List<SizeModel> sizes) {
        this.sizes = sizes;
    }
}
