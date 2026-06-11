package com.example.clipmainjava.Models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SizeModel {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private String category;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return name;
    }

    public static List<SizeModel> parseJsonToArray(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<SizeModel>>(){}.getType();
            return gson.fromJson(json, listType);
        }
        catch (Exception e) {
            return null;
        }
    }
}
