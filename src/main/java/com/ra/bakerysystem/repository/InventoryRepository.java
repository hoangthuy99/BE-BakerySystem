package com.ra.bakerysystem.repository;

import com.ra.bakerysystem.model.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("""
        SELECT COUNT(i)
        FROM Inventory i
        WHERE i.currentQuantity <= i.minThreshold
    """)
    Long countLowStock();


    @Query(value = """
        select i.* from inventories i
        join products p on p.product_id = i.product_id
        join categories c on p.category_id = c.category_id
        where c.category_id in (:listIdCategoryCake) and i.current_quantity <= i.min_threshold
    """, nativeQuery = true)
    List<Inventory> getInventoryForRequestFactory(@Param("listIdCategoryCake") List<Long> listIdCategoryCake);
}
