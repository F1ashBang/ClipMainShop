package com.example.clipmainjava.CartRealize;

import android.content.Context;

import com.example.clipmainjava.Models.CartItem;
import com.example.clipmainjava.Models.ProductModel;

import java.util.Iterator;
import java.util.List;

public class CartManager {
    private List<CartItem> items;
    private final CartStorage storage;

    private static CartManager instance;

    private CartManager(Context context) {
        this.storage = new LocalCartStorage(context);
        this.items = storage.load();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context.getApplicationContext());
        }
        return instance;
    }

    public void add(ProductModel product, int quantity, String size) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(product.getId()) && sameSize(item.getSize(), size)) {
                item.setQuantity(item.getQuantity() + quantity);
                save();
                return;
            }
        }
        items.add(new CartItem(product, quantity, size));
        save();
    }

    public void remove(Long productId) {
        Iterator<CartItem> iterator = items.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().getProduct().getId().equals(productId)) {
                iterator.remove();
                save();
                return;
            }
        }
    }

    public void setQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            remove(productId);
            return;
        }
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                save();
                return;
            }
        }
    }

    public void increase(Long productId) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + 1);
                save();
                return;
            }
        }
    }

    public void decrease(Long productId) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                int newQuantity = item.getQuantity() - 1;
                if (newQuantity <= 0) {
                    remove(productId);
                }
                else {
                    item.setQuantity(newQuantity);
                }
                return;
            }
        }
    }

    public List<CartItem> getItems() {
        return items;
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }


    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public void clear() {
        items.clear();
        storage.clear();
    }
    public void save() {
        storage.save(items);
    }
    private boolean sameSize(String size1, String size2) {
        if (size1 == null && size2 == null) return true;
        if (size1 == null || size2 == null) return false;
        return size1.equals(size2);
    }
}
