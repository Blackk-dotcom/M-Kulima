package com.tradeapp.repository;

import com.tradeapp.model.Order;
import com.tradeapp.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerId(Long buyerId);
    List<Order> findBySellerId(Long sellerId);
    List<Order> findByStatus(OrderStatus status);
}
