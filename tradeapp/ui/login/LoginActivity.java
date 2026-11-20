package com.tradeapp.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tradeapp.R;
import com.tradeapp.network.ApiClient;
import com.tradeapp.ui.dashboard.DashboardActivity;
import com.tradeapp.ui.register.RegisterActivity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerLink = findViewById(R.id.registerLink);

        loginButton.setOnClickListener(v -> login());
        registerLink.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = Map.of("email", email, "password", password);

        ApiClient.getApi(this).login(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveSession(response.body());
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    String detail = "HTTP " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            detail += ": " + response.errorBody().string();
                        }
                    } catch (Exception ignored) {
                        // fall back to just the status code if the error body can't be read
                    }
                    Toast.makeText(LoginActivity.this, "Login failed - " + detail, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Caches the logged-in user's own profile fields locally so screens like
    // Profile can show them instantly without a dedicated GET /me call.
    private void saveSession(Map<String, Object> body) {
        SharedPreferences prefs = getSharedPreferences("trade_app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("jwt_token", (String) body.get("token"));
        if (body.get("userId") != null) {
            editor.putLong("user_id", ((Number) body.get("userId")).longValue());
        }
        editor.putString("user_name", (String) body.get("name"));
        editor.putString("user_role", (String) body.get("role"));
        editor.putString("user_business_name", (String) body.get("businessName"));
        editor.apply();
    }
}
