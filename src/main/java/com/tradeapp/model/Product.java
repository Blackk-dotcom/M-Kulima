package com.tradeapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producer_id", nullable = false)
    private User producer;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private String unit;

    @Column(name = "stock_qty", nullable = false)
    private Integer stockQty = 0;

    // Wholesale-style minimum order quantity (Alibaba-style MOQ) - buyers must
    // order at least this many units in a single order line for this product.
    @Column(name = "min_order_quantity", nullable = false)
    private Integer minOrderQuantity = 1;

    // Absolute or app-resolvable URL to the product photo, set after a
    // separate multipart upload to /api/products/{id}/image.
    @Column(name = "image_url")
    private String imageUrl;
}
