package com.example.clipmainjava.Database;

import com.example.clipmainjava.Models.ImageModel;
import com.example.clipmainjava.Models.LastOrderModel;
import com.example.clipmainjava.Models.OrderModel;
import com.example.clipmainjava.Models.ProductModel;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/check")
    Call<Map<String, Object>> checkPhone(@Query("phone") String phone);

    @POST("/register")
    Call<Map<String, Object>> registerPhone(@Body Map<String, String> body);

    @POST("/verify")
    Call<Map<String, Object>> verifyCode(@Body Map<String, String> body);

    @POST("/login")
    Call<Map<String, Object>> loginPhone(@Body Map<String, String> body);

    @Multipart
    @POST("/upload")
    Call<Map<String, Object>> uploadImages(@Part MultipartBody.Part file);

    @GET("/products")
    Call<List<ProductModel>> getAllProducts();

    @POST("/orders")
    Call<Map<String, Object>> createOrder(@Body Map<String, Object> body);

    @GET("/orders/user/{userId}")
    Call<List<OrderModel>> getUserOrders(@Path("userId") long userId);

    @GET("/orders/{orderId}")
    Call<OrderModel> getOrder(@Path("orderId") long orderId);

    @PUT("/orders/{orderId}/status")
    Call<Map<String, Object>> updateOrderStatus(@Path("orderId") long orderId, @Body Map<String, String> body);

    @GET("orders/user/{userId}/last")
    Call<LastOrderModel> getLastUserOrder(@Path("userId") long userId);

    @GET("/products/search")
    Call<List<ProductModel>> searchProducts(@Query("q") String query);
}
