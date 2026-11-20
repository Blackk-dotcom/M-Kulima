package com.tradeapp.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tradeapp.R;
import com.tradeapp.ui.login.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView nameView = findViewById(R.id.profileName);
        TextView roleView = findViewById(R.id.profileRole);
        Button logoutButton = findViewById(R.id.logoutButton);

        SharedPreferences prefs = getSharedPreferences("trade_app_prefs", MODE_PRIVATE);

        // businessName is optional at registration, so fall back to the
        // account holder's own name if no business name was given.
        String businessName = prefs.getString("user_business_name", null);
        String personalName = prefs.getString("user_name", null);
        String role = prefs.getString("user_role", null);

        nameView.setText(
                (businessName != null && !businessName.isEmpty()) ? businessName
                        : (personalName != null ? personalName : "Your account"));
        roleView.setText("Role: " + (role != null ? role : "Unknown"));

        logoutButton.setOnClickListener(v -> {
            prefs.edit()
                    .remove("jwt_token")
                    .remove("user_id")
                    .remove("user_name")
                    .remove("user_role")
                    .remove("user_business_name")
                    .apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
