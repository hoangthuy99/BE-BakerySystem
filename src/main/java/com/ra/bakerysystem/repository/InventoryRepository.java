package com.ra.bakerysystem.repository;

import com.ra.bakerysystem.model.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("""
        SELECT COUNT(i)
        FROM Inventory i
        WHERE i.currentQuantity <= i.minThreshold
    """)
    Long countLowStock();
}
