package com.tradeapp.repository;

import com.tradeapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProducerId(Long producerId);
    List<Product> findByCategory(String category);
}
