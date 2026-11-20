package com.tradeapp.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tradeapp.R;
import com.tradeapp.model.Product;
import com.tradeapp.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private ProductAdapter adapter;
    private final List<Product> allProducts = new ArrayList<>();
    private String activeCategory = "All";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        RecyclerView recyclerView=findViewById(R.id.productRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter=new ProductAdapter(product -> {
            Intent i=new Intent(this, ProductDetailActivity.class);
            i.putExtra("productId", product.getId()); startActivity(i);
        });
        recyclerView.setAdapter(adapter);

        Button add=findViewById(R.id.addProductButton);
        add.setOnClickListener(v -> startActivity(new Intent(this, AddProductActivity.class)));

        EditText search=findViewById(R.id.searchInput);
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int st,int c,int a){}
            public void onTextChanged(CharSequence s,int st,int b,int c){ applyFilter(s.toString()); }
            public void afterTextChanged(Editable e){}
        });

        loadProducts();
    }

    private void loadProducts() {
        new ProductRepository(this).listProducts(null, null, new ProductRepository.Listener<>() {
            @Override public void onSuccess(List<Product> result) {
                allProducts.clear(); if(result!=null) allProducts.addAll(result);
                buildCategories(); applyFilter("");
            }
            @Override public void onError(String message) { }
        });
    }

    private void buildCategories() {
        android.widget.LinearLayout row=findViewById(R.id.categoryRow);
        row.removeAllViews();
        List<String> cats=new ArrayList<>(); cats.add("All");
        for(Product p:allProducts) if(p.getCategory()!=null && !p.getCategory().isBlank() && !cats.contains(p.getCategory())) cats.add(p.getCategory());
        for(String cat:cats) {
            TextView chip=new TextView(this);
            chip.setText(cat); chip.setTextSize(13); chip.setTextColor(Color.WHITE);
            chip.setGravity(android.view.Gravity.CENTER); chip.setPadding(22,7,22,7);
            chip.setBackgroundResource(R.drawable.bg_chip);
            chip.setTextColor(getColor(R.color.mkulima_text));
            ViewGroup.MarginLayoutParams lp=new ViewGroup.MarginLayoutParams(-2,-2); lp.setMargins(0,0,8,0);
            row.addView(chip,lp);
            chip.setOnClickListener(v->{ activeCategory=cat; applyFilter(((EditText)findViewById(R.id.searchInput)).getText().toString()); });
        }
    }

    private void applyFilter(String query) {
        String q=query==null?"":query.trim().toLowerCase();
        List<Product> filtered=new ArrayList<>();
        for(Product p:allProducts) {
            boolean cat=activeCategory.equals("All") || activeCategory.equalsIgnoreCase(p.getCategory());
            boolean text=q.isEmpty() || (p.getName()!=null && p.getName().toLowerCase().contains(q))
                    || (p.getCategory()!=null && p.getCategory().toLowerCase().contains(q));
            if(cat && text) filtered.add(p);
        }
        adapter.setProducts(filtered);
        ((TextView)findViewById(R.id.resultsTitle)).setText(filtered.size()+" products");
    }

    @Override protected void onResume(){ super.onResume(); }
}
