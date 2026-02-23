package com.ra.bakerysystem.repository;

import com.ra.bakerysystem.common.OrderType;
import com.ra.bakerysystem.model.DTO.OrderResponseDTO;
import com.ra.bakerysystem.model.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /* ================= ORDER LIST ================= */

    @Query("""
        SELECT DISTINCT o
        FROM Order o
        LEFT JOIN FETCH o.items
        WHERE o.orderTime BETWEEN :start AND :end
    """)
    List<Order> findOrdersByDate(
            @Param("start") Instant start,
            @Param("end") Instant end
    );

    @Query("""
        SELECT DISTINCT o
        FROM Order o
        LEFT JOIN FETCH o.items
        WHERE o.orderTime BETWEEN :start AND :end
        AND o.orderType = :orderType
    """)
    List<Order> findOrdersByDateAndType(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("orderType") OrderType orderType
    );

    /* ================= DASHBOARD ================= */

    // Tổng doanh thu hôm nay
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.orderTime BETWEEN :start AND :end
    """)
    Integer getDailySales(
            @Param("start") Instant start,
            @Param("end") Instant end
    );

    // Tổng số đơn hôm nay
    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.orderTime BETWEEN :start AND :end
    """)
    Long getOrderCount(
            @Param("start") Instant start,
            @Param("end") Instant end
    );

    // Doanh thu theo giờ (24h)
    @Query("""
        SELECT HOUR(o.orderTime), COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.orderTime BETWEEN :start AND :end
        GROUP BY HOUR(o.orderTime)
    """)
    List<Object[]> getHourlySales(
            @Param("start") Instant start,
            @Param("end") Instant end
    );




}
