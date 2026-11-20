package com.tradeapp.controller;

import com.tradeapp.model.Order;
import com.tradeapp.model.PaymentStatus;
import com.tradeapp.model.Transaction;
import com.tradeapp.repository.OrderRepository;
import com.tradeapp.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

    public TransactionController(TransactionRepository transactionRepository, OrderRepository orderRepository) {
        this.transactionRepository = transactionRepository;
        this.orderRepository = orderRepository;
    }

    public record CreateTransactionRequest(Long orderId, String paymentMethod) {}

    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody CreateTransactionRequest req) {
        Order order = orderRepository.findById(req.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Transaction tx = new Transaction();
        tx.setOrder(order);
        tx.setAmount(order.getTotalAmount());
        tx.setPaymentMethod(req.paymentMethod());
        tx.setPaymentStatus(PaymentStatus.PENDING);
        return ResponseEntity.ok(transactionRepository.save(tx));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Transaction> getByOrder(@PathVariable Long orderId) {
        return transactionRepository.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Transaction> listByUser(@RequestParam Long userId) {
        return transactionRepository.findByOrder_BuyerIdOrOrder_SellerId(userId, userId);
    }
}
