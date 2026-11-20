package com.tradeapp.service;

import com.tradeapp.dto.OrderDtos.*;
import com.tradeapp.model.*;
import com.tradeapp.repository.OrderRepository;
import com.tradeapp.repository.ProductRepository;
import com.tradeapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {

    // Defines which transitions are legal from each status - prevents skipping steps
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(OrderStatus.class);
    static {
        ALLOWED_TRANSITIONS.put(OrderStatus.PENDING, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED));
        ALLOWED_TRANSITIONS.put(OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class));
        ALLOWED_TRANSITIONS.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
    }

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                         UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order createOrder(Long buyerId, CreateOrderRequest req) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));
        User seller = userRepository.findById(req.sellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        Order order = new Order();
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : req.items()) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemReq.productId()));
            if (product.getStockQty() < itemReq.quantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
            if (itemReq.quantity() < product.getMinOrderQuantity()) {
                throw new IllegalStateException("Minimum order quantity for " + product.getName()
                        + " is " + product.getMinOrderQuantity() + " " + product.getUnit());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemReq.quantity());
            item.setUnitPrice(product.getUnitPrice());
            BigDecimal subtotal = product.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));
            item.setSubtotal(subtotal);

            order.getItems().add(item);
            total = total.add(subtotal);

            product.setStockQty(product.getStockQty() - itemReq.quantity());
            productRepository.save(product);
        }
        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.get(order.getStatus());
        if (allowed == null || !allowed.contains(newStatus)) {
            throw new IllegalStateException(
                    "Cannot transition order from " + order.getStatus() + " to " + newStatus);
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(Instant.now());
        return orderRepository.save(order);
    }

    public OrderResponse toResponse(Order order) {
        var items = order.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getProduct().getId(), i.getProduct().getName(),
                        i.getQuantity(), i.getUnitPrice(), i.getSubtotal()))
                .collect(Collectors.toList());
        return new OrderResponse(order.getId(), order.getBuyer().getId(), order.getSeller().getId(),
                order.getStatus(), order.getTotalAmount(), items);
    }
}
