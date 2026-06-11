package com.example.clipmainjava.CartRealize;

import com.example.clipmainjava.Models.CartItem;

import java.util.List;

public interface CartStorage {
    void save(List<CartItem> items);
    List<CartItem> load();
    void clear();
}
