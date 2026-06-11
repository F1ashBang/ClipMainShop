package com.example.clipmainjava.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DaDataResponse {
    @SerializedName("suggestions")
    public List<DaDataSuggestion> suggestions;
}
