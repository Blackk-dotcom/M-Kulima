package com.tradeapp.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tradeapp.R;
import com.tradeapp.model.Order;
import com.tradeapp.model.OrderStatus;
import com.tradeapp.repository.OrderRepository;
import com.tradeapp.repository.ProductRepository.Listener;
import com.tradeapp.ui.transaction.TransactionActivity;

public class OrderDetailActivity extends AppCompatActivity {

    private long orderId;
    private Order currentOrder;
    private OrderRepository orderRepository;
    private TextView statusView;
    private TextView totalView;

    // Mirrors the backend's OrderService state machine, just for picking the
    // "next" status to advance to from the UI.
    private OrderStatus nextStatus(OrderStatus current) {
        switch (current) {
            case PENDING: return OrderStatus.CONFIRMED;
            case CONFIRMED: return OrderStatus.SHIPPED;
            case SHIPPED: return OrderStatus.DELIVERED;
            default: return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getLongExtra("orderId", -1);
        orderRepository = new OrderRepository(this);

        statusView = findViewById(R.id.orderDetailStatus);
        totalView = findViewById(R.id.orderDetailTotal);
        Button advanceButton = findViewById(R.id.advanceStatusButton);
        Button payButton = findViewById(R.id.payNowButton);

        loadOrder();

        advanceButton.setOnClickListener(v -> {
            if (currentOrder == null) return;
            OrderStatus next = nextStatus(currentOrder.getStatus());
            if (next == null) {
                Toast.makeText(this, "Order is already at a final status", Toast.LENGTH_SHORT).show();
                return;
            }
            orderRepository.updateStatus(orderId, next.name(), new Listener<Order>() {
                @Override
                public void onSuccess(Order result) {
                    currentOrder = result;
                    render();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(OrderDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        payButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TransactionActivity.class);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
        });
    }

    private void loadOrder() {
        // Reuses the order list endpoint result if navigated with data already loaded
        // in a full app; here we re-fetch directly for simplicity.
        orderRepository.listOrders(null, null, new Listener<java.util.List<Order>>() {
            @Override
            public void onSuccess(java.util.List<Order> result) {
                for (Order o : result) {
                    if (o.getId() != null && o.getId() == orderId) {
                        currentOrder = o;
                        render();
                        return;
                    }
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(OrderDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void render() {
        statusView.setText("Status: " + (currentOrder.getStatus() == null ? "UNKNOWN" : currentOrder.getStatus().name()));
        totalView.setText("Total: " + (currentOrder.getTotalAmount() == null ? "0.00" : currentOrder.getTotalAmount()));
    }
}
