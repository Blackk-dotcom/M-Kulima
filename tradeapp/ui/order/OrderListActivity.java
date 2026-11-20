package com.tradeapp.ui.order;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tradeapp.R;
import com.tradeapp.model.Order;
import com.tradeapp.repository.OrderRepository;
import com.tradeapp.repository.ProductRepository.Listener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        RecyclerView recyclerView = findViewById(R.id.orderRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        OrderAdapter adapter = new OrderAdapter(order -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("orderId", order.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("trade_app_prefs", MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // A user can be a buyer on some orders and a seller on others (e.g. a
        // wholesaler buys from producers and sells to retailers), so fetch both
        // roles and merge them into a single, newest-first list.
        OrderRepository repo = new OrderRepository(this);
        List<Order> combined = new ArrayList<>();
        int[] pending = {2};

        Listener<List<Order>> onPart = new Listener<List<Order>>() {
            @Override
            public void onSuccess(List<Order> result) {
                combined.addAll(result);
                pending[0]--;
                if (pending[0] == 0) {
                    combined.sort(Comparator.comparing(Order::getId).reversed());
                    adapter.setOrders(combined);
                }
            }

            @Override
            public void onError(String message) {
                pending[0]--;
                Toast.makeText(OrderListActivity.this, message, Toast.LENGTH_SHORT).show();
                if (pending[0] == 0 && !combined.isEmpty()) {
                    combined.sort(Comparator.comparing(Order::getId).reversed());
                    adapter.setOrders(combined);
                }
            }
        };

        repo.listOrders(userId, null, onPart);
        repo.listOrders(null, userId, onPart);
    }
}
