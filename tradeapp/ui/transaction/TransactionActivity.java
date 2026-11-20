package com.tradeapp.ui.transaction;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tradeapp.R;
import com.tradeapp.network.ApiClient;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        TextView statusView = findViewById(R.id.transactionStatus);
        TextView amountView = findViewById(R.id.transactionAmount);

        long orderId = getIntent().getLongExtra("orderId", -1);

        // Kicks off a PENDING transaction record on the backend. In production,
        // this is where you'd launch the Stripe/Razorpay/PayPal SDK checkout flow
        // and only mark the transaction PAID once the gateway confirms it.
        Map<String, Object> body = Map.of("orderId", orderId, "paymentMethod", "mobile_money");
        ApiClient.getApi(this).createTransaction(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    statusView.setText("Payment status: " + response.body().get("paymentStatus"));
                    amountView.setText("Amount: " + response.body().get("amount"));
                } else {
                    Toast.makeText(TransactionActivity.this, "Failed to start payment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(TransactionActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
