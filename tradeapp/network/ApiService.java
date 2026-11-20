package com.tradeapp.network;

import com.tradeapp.model.Order;
import com.tradeapp.model.Product;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ---- Auth ----
    @POST("api/auth/register")
    Call<Map<String, Object>> register(@Body Map<String, Object> body);

    @POST("api/auth/login")
    Call<Map<String, Object>> login(@Body Map<String, String> body);

    // ---- Products ----
    @GET("api/products")
    Call<List<Product>> getProducts(@Query("producerId") Long producerId,
                                     @Query("category") String category);

    @GET("api/products/{id}")
    Call<Product> getProduct(@Path("id") Long id);

    @POST("api/products")
    Call<Product> createProduct(@Body Product product);

    @Multipart
    @POST("api/products/{id}/image")
    Call<Product> uploadProductImage(@Path("id") Long id,
                                     @Part MultipartBody.Part image);

    // ---- Orders ----
    @POST("api/orders")
    Call<Order> createOrder(@Body Map<String, Object> body);

    @GET("api/orders/{id}")
    Call<Order> getOrder(@Path("id") Long id);

    @GET("api/orders")
    Call<List<Order>> getOrders(@Query("buyerId") Long buyerId, @Query("sellerId") Long sellerId);

    @PATCH("api/orders/{id}/status")
    Call<Order> updateOrderStatus(@Path("id") Long id, @Body Map<String, String> body);

    // ---- Transactions ----
    @POST("api/transactions")
    Call<Map<String, Object>> createTransaction(@Body Map<String, Object> body);

    @GET("api/transactions/{orderId}")
    Call<Map<String, Object>> getTransaction(@Path("orderId") Long orderId);
}
