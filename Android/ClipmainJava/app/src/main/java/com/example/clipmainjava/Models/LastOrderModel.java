package com.example.clipmainjava.Models;

import com.google.gson.annotations.SerializedName;

public class LastOrderModel {
    @SerializedName("hasOrder")
    private boolean hasOrder;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("middleName")
    private String middleName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("address")
    private String address;

    // Геттеры
    public boolean isHasOrder() { return hasOrder; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getMiddleName() { return middleName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
}
