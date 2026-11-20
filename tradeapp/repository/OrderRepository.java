package com.tradeapp.repository;

import android.content.Context;

import com.tradeapp.model.Order;
import com.tradeapp.network.ApiClient;
import com.tradeapp.network.ApiService;
import com.tradeapp.repository.ProductRepository.Listener;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {

    private final ApiService api;

    public OrderRepository(Context context) {
        this.api = ApiClient.getApi(context);
    }

    public void createOrder(Long sellerId, List<Map<String, Object>> items, Listener<Order> listener) {
        Map<String, Object> body = Map.of("sellerId", sellerId, "items", items);
        api.createOrder(body).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to create order: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void listOrders(Long buyerId, Long sellerId, Listener<List<Order>> listener) {
        api.getOrders(buyerId, sellerId).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to load orders: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void updateStatus(Long orderId, String newStatus, Listener<Order> listener) {
        api.updateOrderStatus(orderId, Map.of("status", newStatus)).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to update order: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }
}
