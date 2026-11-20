package com.tradeapp.model;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private Long id;
    private Long buyerId;
    private Long sellerId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String createdAt;
    private String updatedAt;
    private List<OrderItem> items;

    public Long getId() { return id; }
    public Long getBuyerId() { return buyerId; }
    public Long getSellerId() { return sellerId; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public List<OrderItem> getItems() { return items; }
}
