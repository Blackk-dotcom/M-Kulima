package com.tradeapp.cart;

import java.math.BigDecimal;

public class CartItem {
    private final Long productId;
    private final String productName;
    private final Long sellerId;
    private final BigDecimal unitPrice;
    private final String unit;
    private final Integer minOrderQuantity;
    private int quantity;

    public CartItem(Long productId, String productName, Long sellerId, BigDecimal unitPrice,
                     String unit, Integer minOrderQuantity, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.sellerId = sellerId;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.minOrderQuantity = minOrderQuantity;
        this.quantity = quantity;
    }

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Long getSellerId() { return sellerId; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public String getUnit() { return unit; }
    public Integer getMinOrderQuantity() { return minOrderQuantity; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
