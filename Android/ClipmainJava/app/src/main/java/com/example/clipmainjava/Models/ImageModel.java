package com.example.clipmainjava.Models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ImageModel {

    @SerializedName("id")
    private Long id;

    @SerializedName("fileName")
    private String fileName;
    @SerializedName("filePath")
    private String filePath;
    @SerializedName("uploadedAt")
    private String uploadedAt;

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName != null ? fileName : filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }

    public static List<ImageModel> parseJsonArray(String json) {
        if (json == null || json.isEmpty()) return null;

        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ImageModel>>(){}.getType();
            return gson.fromJson(json, listType);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getImageURL() {
        if (fileName == null) {
            return null;
        }
        return "http://192.168.3.64:8080/images/" + fileName;
    }
}
