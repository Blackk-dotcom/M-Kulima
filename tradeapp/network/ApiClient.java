package com.tradeapp.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Point this at your deployed Spring Boot backend.
    // Use 10.0.2.2 instead of localhost when testing against a local server from the Android emulator.
    // On a real phone, use your PC's LAN IP instead (both devices must be on the same WiFi).
    private static final String BASE_URL = "http://192.168.88.237:8080/";

    private static Retrofit retrofit;

    public static ApiService getApi(Context context) {
        if (retrofit == null) {
            SharedPreferences prefs = context.getApplicationContext()
                    .getSharedPreferences("trade_app_prefs", Context.MODE_PRIVATE);

            Interceptor authInterceptor = chain -> {
                Request original = chain.request();
                String token = prefs.getString("jwt_token", null);
                if (token == null) {
                    return chain.proceed(original);
                }
                Request authorized = original.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(authorized);
            };

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
