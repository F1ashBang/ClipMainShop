package com.example.clipmainjava.Models;

public class CartItem {
    private Long id;
    private ProductModel product;
    private String size;
    private int quantity;
    private long AddedAt;

    public CartItem(ProductModel product, int quantity, String size) {
        AddedAt = System.currentTimeMillis();
        this.quantity = quantity;
        this.size = size;
        this.product = product;
    }

    public CartItem() {
        AddedAt = System.currentTimeMillis();
    }

    public Long getId() {
        return id;
    }

    public ProductModel getProduct() {
        return product;
    }

    public void setProduct(ProductModel product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public long getAddedAt() {
        return AddedAt;
    }

    public void setAddedAt(long addedAt) {
        AddedAt = addedAt;
    }

    public double getTotalPrice() {
        if (product == null || product.getPrice() == null) {
            return 0;
        }
        try {
            return product.getPrice() * quantity;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
