package com.tradeapp.ui.register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tradeapp.R;
import com.tradeapp.network.ApiClient;
import com.tradeapp.ui.dashboard.DashboardActivity;
import com.tradeapp.ui.login.LoginActivity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput, businessNameInput, phoneInput, emailInput, passwordInput;
    private RadioGroup roleRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.nameInput);
        businessNameInput = findViewById(R.id.businessNameInput);
        phoneInput = findViewById(R.id.phoneInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);

        Button registerButton = findViewById(R.id.registerButton);
        TextView loginLink = findViewById(R.id.loginLink);
        CheckBox showPasswordCheckbox = findViewById(R.id.showPasswordCheckbox);

        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int cursorPosition = passwordInput.getSelectionStart();
            if (isChecked) {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            // Changing inputType resets the cursor to the start - put it back where the user was typing.
            passwordInput.setSelection(cursorPosition);
        });

        registerButton.setOnClickListener(v -> register());
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private String selectedRole() {
        int id = roleRadioGroup.getCheckedRadioButtonId();
        if (id == R.id.roleProducer) return "PRODUCER";
        if (id == R.id.roleRetailer) return "RETAILER";
        return "WHOLESALER";
    }

    private void register() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String businessName = businessNameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Name, email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> body = Map.of(
                "name", name,
                "email", email,
                "password", password,
                "role", selectedRole(),
                "businessName", businessName,
                "phone", phone
        );

        ApiClient.getApi(this).register(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveSession(response.body());
                    startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
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
                    Toast.makeText(RegisterActivity.this, "Registration failed - " + detail, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Caches the newly-created user's own profile fields locally so screens like
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
