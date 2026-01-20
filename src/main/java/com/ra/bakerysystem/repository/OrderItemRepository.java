package com.ra.bakerysystem.repository;

import com.ra.bakerysystem.model.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Top selling products
    @Query("""
        SELECT oi.product.id, oi.product.name, SUM(oi.quantity)
        FROM OrderItem oi
        GROUP BY oi.product.id, oi.product.name
        ORDER BY SUM(oi.quantity) DESC
    """)
    List<Object[]> findTopProducts(Pageable pageable);

    // Tổng số bán trong khoảng thời gian (dùng cho hôm nay)
    @Query("""
        SELECT COALESCE(SUM(oi.quantity), 0)
        FROM OrderItem oi
        WHERE oi.product.id = :productId
          AND oi.order.orderTime >= :start
          AND oi.order.orderTime < :end
    """)
    int getSoldInRange(
            @Param("productId") Long productId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
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
}
