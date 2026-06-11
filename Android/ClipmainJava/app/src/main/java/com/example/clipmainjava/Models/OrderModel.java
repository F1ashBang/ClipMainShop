package com.example.clipmainjava.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderModel {

    @SerializedName("id")
    private Long id;

    @SerializedName("status")
    private String status;

    @SerializedName("totalPrice")
    private Double totalPrice;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("address")
    private String address;

    @SerializedName("phoneAtOrder")
    private String phoneAtOrder;

    @SerializedName("items")
    private List<OrderItemModel> items;

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneAtOrder() {
        return phoneAtOrder;
    }

    public List<OrderItemModel> getItems() {
        return items;
    }

    public int getItemsCount() {
        return items != null ? items.size() : 0;
    }
}
