package com.example.clipmainjava.Database;

import com.example.clipmainjava.Models.DaDataResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface DaDataApiService {
    @GET("https://suggestions.dadata.ru/suggestions/api/4_1/rs/suggest/address")
    Call<DaDataResponse> suggestAddress(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("count") int count
    );
}
