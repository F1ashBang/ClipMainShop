package com.example.clipmainjava.Database;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.3.64:8080/";
    private static Retrofit retrofit = null;
    private static final String DADATA_BASE_URL = "https://suggestions.dadata.ru/";
    private static DaDataApiService daDataService = null;
    public static ApiService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    public static DaDataApiService getDaDataService() {
        if (daDataService == null) {
            Retrofit retrofitDaData = new Retrofit.Builder()
                    .baseUrl(DADATA_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            daDataService = retrofitDaData.create(DaDataApiService.class);
        }
        return daDataService;
    }
}
