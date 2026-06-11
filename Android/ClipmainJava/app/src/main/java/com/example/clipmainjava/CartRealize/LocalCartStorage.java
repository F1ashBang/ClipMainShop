package com.example.clipmainjava.CartRealize;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.clipmainjava.Models.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocalCartStorage implements CartStorage{

    private static final String PREFS_NAME = "cart_prefs";
    private static final String KEY_CART = "cart_items";
    private final SharedPreferences prefs;
    private final Gson gson;

    public LocalCartStorage(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    @Override
    public void save(List<CartItem> items) {
        String json = gson.toJson(items);
        prefs.edit().putString(KEY_CART, json).apply();
    }

    @Override
    public List<CartItem> load() {
        String json = prefs.getString(KEY_CART, null);
        if (json == null) return new ArrayList<>();

        Type listType = new TypeToken<List<CartItem>>(){}.getType();
        try {
            return gson.fromJson(json, listType);
        }
        catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void clear() {
        prefs.edit().remove(KEY_CART).apply();
    }
}
