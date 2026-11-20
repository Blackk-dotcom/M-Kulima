package com.tradeapp.dto;

import com.tradeapp.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public class OrderDtos {

    public record OrderItemRequest(Long productId, Integer quantity) {}

    public record CreateOrderRequest(Long sellerId, List<OrderItemRequest> items) {}

    public record UpdateStatusRequest(OrderStatus status) {}

    public record OrderItemResponse(Long productId, String productName, Integer quantity,
                                     BigDecimal unitPrice, BigDecimal subtotal) {}

    public record OrderResponse(Long id, Long buyerId, Long sellerId, OrderStatus status,
                                 BigDecimal totalAmount, List<OrderItemResponse> items) {}
}
