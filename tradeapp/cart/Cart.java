package com.tradeapp.cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the buyer's in-progress order draft in memory for the lifetime of the
 * app process. Wholesale orders can span multiple products from the same
 * seller, so items are keyed by productId and merged on repeat "Add to Order".
 * Cart contents are grouped by sellerId at checkout since each backend Order
 * belongs to exactly one seller.
 */
public class Cart {

    private static final Cart INSTANCE = new Cart();

    private final Map<Long, CartItem> itemsByProductId = new LinkedHashMap<>();

    private Cart() {}

    public static Cart getInstance() {
        return INSTANCE;
    }

    public void addOrUpdate(CartItem item) {
        CartItem existing = itemsByProductId.get(item.getProductId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
        } else {
            itemsByProductId.put(item.getProductId(), item);
        }
    }

    public void remove(Long productId) {
        itemsByProductId.remove(productId);
    }

    public void clear() {
        itemsByProductId.clear();
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(itemsByProductId.values());
    }

    public int getItemCount() {
        return itemsByProductId.size();
    }

    public BigDecimal getGrandTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : itemsByProductId.values()) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    /** Groups cart items by seller, since each order placed hits one seller at a time. */
    public Map<Long, List<CartItem>> groupBySeller() {
        Map<Long, List<CartItem>> grouped = new LinkedHashMap<>();
        for (CartItem item : itemsByProductId.values()) {
            grouped.computeIfAbsent(item.getSellerId(), k -> new ArrayList<>()).add(item);
        }
        return grouped;
    }
}
