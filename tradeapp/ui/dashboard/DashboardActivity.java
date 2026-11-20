package com.tradeapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.tradeapp.R;
import com.tradeapp.ui.cart.CartActivity;
import com.tradeapp.ui.order.OrderListActivity;
import com.tradeapp.ui.product.ProductListActivity;
import com.tradeapp.ui.profile.ProfileActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button viewProducts = findViewById(R.id.viewProductsButton);
        Button viewOrders = findViewById(R.id.viewOrdersButton);
        Button viewCart = findViewById(R.id.viewCartButton);
        Button viewProfile = findViewById(R.id.viewProfileButton);

        viewProducts.setOnClickListener(v ->
                startActivity(new Intent(this, ProductListActivity.class)));
        viewOrders.setOnClickListener(v ->
                startActivity(new Intent(this, OrderListActivity.class)));
        viewCart.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
        viewProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}
