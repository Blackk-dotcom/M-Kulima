package com.tradeapp.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tradeapp.R;
import com.tradeapp.cart.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnRemoveClickListener {
        void onRemove(CartItem item);
    }

    private List<CartItem> items = new ArrayList<>();
    private final OnRemoveClickListener listener;

    public CartAdapter(OnRemoveClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.name.setText(item.getProductName());
        holder.qty.setText(item.getQuantity() + " " + item.getUnit() + " (MOQ " + item.getMinOrderQuantity() + ")");
        holder.subtotal.setText("Subtotal: " + item.getSubtotal());
        holder.remove.setOnClickListener(v -> listener.onRemove(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, qty, subtotal, remove;

        CartViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cartItemName);
            qty = itemView.findViewById(R.id.cartItemQty);
            subtotal = itemView.findViewById(R.id.cartItemSubtotal);
            remove = itemView.findViewById(R.id.cartItemRemove);
        }
    }
}
