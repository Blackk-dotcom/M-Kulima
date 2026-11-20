package com.tradeapp.repository;

import com.tradeapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByOrderId(Long orderId);
    List<Transaction> findByOrder_BuyerIdOrOrder_SellerId(Long buyerId, Long sellerId);
}
