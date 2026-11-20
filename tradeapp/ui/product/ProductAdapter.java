package com.tradeapp.ui.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.tradeapp.R;
import com.tradeapp.model.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    public interface OnProductClickListener { void onProductClick(Product product); }
    private List<Product> products = new ArrayList<>();
    private final OnProductClickListener listener;

    public ProductAdapter(OnProductClickListener listener) { this.listener = listener; }
    public void setProducts(List<Product> products) {
        this.products = products == null ? new ArrayList<>() : new ArrayList<>(products);
        notifyDataSetChanged();
    }
    @NonNull @Override public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull ProductViewHolder h, int position) {
        Product p = products.get(position);
        h.name.setText(p.getName());
        h.price.setText("KES " + p.getUnitPrice() + " / " + p.getUnit());
        h.stock.setText("In stock • " + p.getStockQty() + " " + p.getUnit());
        h.moq.setText("Min " + p.getMinOrderQuantity() + " " + p.getUnit());
        Glide.with(h.image.getContext()).load(p.getImageUrl())
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder).centerCrop().into(h.image);
        h.itemView.setOnClickListener(v -> listener.onProductClick(p));
    }
    @Override public int getItemCount() { return products.size(); }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, stock, moq; ImageView image;
        ProductViewHolder(View v) {
            super(v);
            image=v.findViewById(R.id.productImage); name=v.findViewById(R.id.productName);
            price=v.findViewById(R.id.productPrice); stock=v.findViewById(R.id.productStock);
            moq=v.findViewById(R.id.productMoq);
        }
    }
}
