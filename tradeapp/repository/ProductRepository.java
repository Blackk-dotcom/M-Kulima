package com.tradeapp.repository;

import android.content.Context;

import com.tradeapp.model.Product;
import com.tradeapp.network.ApiClient;
import com.tradeapp.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ProductRepository {

    private final ApiService api;

    public ProductRepository(Context context) {
        this.api = ApiClient.getApi(context);
    }

    public interface Listener<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public void listProducts(Long producerId, String category, Listener<List<Product>> listener) {
        api.getProducts(producerId, category).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, retrofit2.Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to load products: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void createProduct(Product product, Listener<Product> listener) {
        api.createProduct(product).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, retrofit2.Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to create product: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }
}
