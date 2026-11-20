package com.tradeapp.model;

import java.math.BigDecimal;

public class Product {
    private Long id;
    private Long producerId;
    private String name;
    private String category;
    private BigDecimal unitPrice;
    private String unit;
    private Integer stockQty;
    private Integer minOrderQuantity;
    private String imageUrl;

    public Long getId() { return id; }
    public Long getProducerId() { return producerId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public String getUnit() { return unit; }
    public Integer getStockQty() { return stockQty; }
    public Integer getMinOrderQuantity() { return minOrderQuantity == null ? 1 : minOrderQuantity; }
    public String getImageUrl() { return imageUrl; }

    public void setProducerId(Long producerId) { this.producerId = producerId; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setStockQty(Integer stockQty) { this.stockQty = stockQty; }
    public void setMinOrderQuantity(Integer minOrderQuantity) { this.minOrderQuantity = minOrderQuantity; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
