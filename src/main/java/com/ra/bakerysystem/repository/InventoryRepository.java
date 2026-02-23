package com.ra.bakerysystem.repository;

import com.ra.bakerysystem.model.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    Optional<Inventory> findByProductId(Long productId);
    boolean existsByProductId(Long productId);
    List<Inventory> findByProductIdIn(List<Long> productIds);


    @Modifying
    @Query("""
    UPDATE Inventory i
    SET i.currentQuantity = 20
    WHERE i.product.type = 'FOOD'
""")
    void resetFoodInventory();
    @Modifying
    @Query("""
    UPDATE Inventory i
    SET i.currentQuantity = 100
    WHERE i.product.type IN ('ALCOHOL', 'DRINK')
""")
    void resetDrinkInventory();





}
