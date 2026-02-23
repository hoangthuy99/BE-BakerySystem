package com.ra.bakerysystem.repository;

import com.ra.bakerysystem.model.DTO.OrderItemRequestDTO;
import com.ra.bakerysystem.model.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
        SELECT oi.product.id, oi.product.name, SUM(oi.quantity)
        FROM OrderItem oi
        GROUP BY oi.product.id, oi.product.name
        ORDER BY SUM(oi.quantity) DESC
    """)
    List<Object[]> findTopProducts(Pageable pageable);


    @Query(value = """
    select oi.* from order_items oi
    join orders o on oi.order_id = o.id
    where (:dateTime is null or DATE(o.order_time) = :dateTime)
      and oi.product_id = :productId
""", nativeQuery = true)
    List<OrderItem> getOrderItemsByOrderTime(
            @Param("dateTime") LocalDate dateTime,
            @Param("productId") Long productId
    );

}
