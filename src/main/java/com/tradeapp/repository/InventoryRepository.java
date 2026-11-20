package com.tradeapp.repository;

import com.tradeapp.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByOwnerId(Long ownerId);
}
