package com.ra.bakerysystem.repository;

import com.ra.bakerysystem.model.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    SELECT COALESCE(SUM(oi.quantity), 0)
    FROM order_items oi
    JOIN orders o ON oi.order_id = o.order_id
    WHERE oi.product_id = :productId
      AND o.order_time >=
        CASE 
            WHEN :type = 'DAY' THEN :date
            WHEN :type = 'MONTH' THEN DATE_FORMAT(:date, '%Y-%m-01')
            WHEN :type = 'YEAR' THEN DATE_FORMAT(:date, '%Y-01-01')
        END
      AND o.order_time <
        CASE
            WHEN :type = 'DAY' THEN DATE_ADD(:date, INTERVAL 1 DAY)
            WHEN :type = 'MONTH' THEN DATE_ADD(DATE_FORMAT(:date, '%Y-%m-01'), INTERVAL 1 MONTH)
            WHEN :type = 'YEAR' THEN DATE_ADD(DATE_FORMAT(:date, '%Y-01-01'), INTERVAL 1 YEAR)
        END
""", nativeQuery = true)
    int getSoldByProductAndType(
        @Param("productId") Long productId,
        @Param("date") LocalDate date,
        @Param("type") String type
    );

    // Tổng số lượng đã bán của sản phẩm (ALL TIME)
    @Query("""
        SELECT COALESCE(SUM(oi.quantity), 0)
        FROM OrderItem oi
        WHERE oi.product.id = :productId
    """)
    long getTotalSold(@Param("productId") Long productId);

    // Ngày bán đầu tiên
    @Query("""
        SELECT MIN(oi.order.orderTime)
        FROM OrderItem oi
        WHERE oi.product.id = :productId
    """)
    LocalDateTime getFirstSoldDate(@Param("productId") Long productId);

    // Ngày bán cuối cùng
    @Query("""
        SELECT MAX(oi.order.orderTime)
        FROM OrderItem oi
        WHERE oi.product.id = :productId
    """)
    LocalDateTime getLastSoldDate(@Param("productId") Long productId);


    @Query(value = """
        select oi.* from order_items oi left join bakery_system.orders o on oi.order_id = o.order_id
        where (:dateTime is null or DATE(o.order_time) = :dateTime) and oi.product_id = :productId
        """, nativeQuery = true)
    List<OrderItem> getOrderItemsByOrderTime(@Param("dateTime") LocalDate dateTime, @Param("productId") Long productId);
}
