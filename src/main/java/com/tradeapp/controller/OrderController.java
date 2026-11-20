package com.tradeapp.controller;

import com.tradeapp.dto.OrderDtos.*;
import com.tradeapp.model.Order;
import com.tradeapp.model.User;
import com.tradeapp.repository.OrderRepository;
import com.tradeapp.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest req, Authentication auth) {
        User buyer = (User) auth.getPrincipal();
        Order order = orderService.createOrder(buyer.getId(), req);
        return ResponseEntity.ok(orderService.toResponse(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(orderService::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<OrderResponse> list(@RequestParam(required = false) Long buyerId,
                                     @RequestParam(required = false) Long sellerId) {
        List<Order> orders;
        if (buyerId != null) orders = orderRepository.findByBuyerId(buyerId);
        else if (sellerId != null) orders = orderRepository.findBySellerId(sellerId);
        else orders = orderRepository.findAll();
        return orders.stream().map(orderService::toResponse).toList();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id,
                                                        @RequestBody UpdateStatusRequest req) {
        Order order = orderService.updateStatus(id, req.status());
        return ResponseEntity.ok(orderService.toResponse(order));
    }
}
