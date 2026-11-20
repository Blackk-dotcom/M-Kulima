package com.tradeapp.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tradeapp.R;
import com.bumptech.glide.Glide;
import com.tradeapp.cart.Cart;
import com.tradeapp.cart.CartItem;
import com.tradeapp.network.ApiClient;

import com.tradeapp.ui.cart.CartActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private com.tradeapp.model.Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageView imageView = findViewById(R.id.detailImage);
        TextView nameView = findViewById(R.id.detailName);
        TextView priceView = findViewById(R.id.detailPrice);
        TextView moqView = findViewById(R.id.detailMoq);
        TextView stockView = findViewById(R.id.detailStock);
        EditText quantityInput = findViewById(R.id.quantityInput);
        Button addButton = findViewById(R.id.addToOrderButton);

        long productId = getIntent().getLongExtra("productId", -1);
        ApiClient.getApi(this).getProduct(productId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<com.tradeapp.model.Product> call,
                                    Response<com.tradeapp.model.Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    product = response.body();
                    Glide.with(ProductDetailActivity.this).load(product.getImageUrl()).placeholder(R.drawable.bg_image_placeholder).error(R.drawable.bg_image_placeholder).into(imageView);
                    nameView.setText(product.getName());
                    priceView.setText(product.getUnitPrice() + " / " + product.getUnit());
                    // Alibaba-style: show the minimum order quantity and pre-fill the
                    // quantity field with it so the buyer sees the floor immediately.
                    moqView.setText("Min. Order: " + product.getMinOrderQuantity() + " " + product.getUnit());
                    stockView.setText("In stock: " + product.getStockQty() + " " + product.getUnit());
                    quantityInput.setText(String.valueOf(product.getMinOrderQuantity()));
                }
            }

            @Override
            public void onFailure(Call<com.tradeapp.model.Product> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Failed to load product", Toast.LENGTH_SHORT).show();
            }
        });

        // Pushes (productId, quantity) into the shared in-memory Cart, then lets the
        // buyer keep browsing or jump straight to checkout.
        addButton.setOnClickListener(v -> {
            if (product == null) return;

            int quantity;
            try {
                quantity = Integer.parseInt(quantityInput.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter a valid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            if (quantity < product.getMinOrderQuantity()) {
                Toast.makeText(this, "Minimum order for this product is "
                        + product.getMinOrderQuantity() + " " + product.getUnit(), Toast.LENGTH_SHORT).show();
                return;
            }

            CartItem cartItem = new CartItem(product.getId(), product.getName(), product.getProducerId(),
                    product.getUnitPrice(), product.getUnit(), product.getMinOrderQuantity(), quantity);
            Cart.getInstance().addOrUpdate(cartItem);

            Toast.makeText(this, "Added " + quantity + " " + product.getUnit() + " to cart", Toast.LENGTH_SHORT).show();
        });

        Button goToCartButton = findViewById(R.id.goToCartButton);
        goToCartButton.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
    }
}
