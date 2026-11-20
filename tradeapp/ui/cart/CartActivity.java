package com.tradeapp.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tradeapp.R;
import com.tradeapp.cart.Cart;
import com.tradeapp.cart.CartItem;
import com.tradeapp.model.Order;
import com.tradeapp.repository.OrderRepository;
import com.tradeapp.repository.ProductRepository.Listener;
import com.tradeapp.ui.order.OrderListActivity;

import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private CartAdapter adapter;
    private TextView grandTotalView;
    private TextView sellerNoteView;
    private OrderRepository orderRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        orderRepository = new OrderRepository(this);

        RecyclerView recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(item -> {
            Cart.getInstance().remove(item.getProductId());
            render();
        });
        recyclerView.setAdapter(adapter);

        grandTotalView = findViewById(R.id.cartGrandTotal);
        sellerNoteView = findViewById(R.id.cartSellerNote);
        Button placeOrderButton = findViewById(R.id.placeOrderButton);
        placeOrderButton.setOnClickListener(v -> placeOrder());

        render();
    }

    @Override
    protected void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        Cart cart = Cart.getInstance();
        adapter.setItems(cart.getItems());
        grandTotalView.setText("Grand total: " + cart.getGrandTotal());

        int sellerCount = cart.groupBySeller().size();
        if (sellerCount > 1) {
            sellerNoteView.setText("Items are from " + sellerCount
                    + " different sellers and will be placed as " + sellerCount + " separate orders.");
        } else {
            sellerNoteView.setText("");
        }
    }

    private void placeOrder() {
        Cart cart = Cart.getInstance();
        Map<Long, List<CartItem>> grouped = cart.groupBySeller();

        if (grouped.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // One backend Order per seller, since Order.sellerId is a single party.
        // Wait for every group to resolve before clearing the cart / navigating.
        int[] remaining = {grouped.size()};
        boolean[] hadError = {false};

        for (Map.Entry<Long, List<CartItem>> entry : grouped.entrySet()) {
            Long sellerId = entry.getKey();
            List<Map<String, Object>> items = entry.getValue().stream()
                    .map(ci -> (Map<String, Object>) (Map<String, ?>) Map.of(
                            "productId", ci.getProductId(),
                            "quantity", ci.getQuantity()))
                    .toList();

            orderRepository.createOrder(sellerId, items, new Listener<Order>() {
                @Override
                public void onSuccess(Order result) {
                    remaining[0]--;
                    checkDone();
                }

                @Override
                public void onError(String message) {
                    hadError[0] = true;
                    remaining[0]--;
                    Toast.makeText(CartActivity.this, "Order failed: " + message, Toast.LENGTH_SHORT).show();
                    checkDone();
                }

                private void checkDone() {
                    if (remaining[0] == 0) {
                        if (!hadError[0]) {
                            Cart.getInstance().clear();
                            Toast.makeText(CartActivity.this, "Order(s) placed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CartActivity.this, OrderListActivity.class));
                            finish();
                        } else {
                            render();
                        }
                    }
                }
            });
        }
    }
}
