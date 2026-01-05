package com.ra.bakerysystem.repository;

import com.ra.bakerysystem.common.OrderType;
import com.ra.bakerysystem.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByOrderTimeBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    // Tổng doanh thu hôm nay
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.orderTime BETWEEN :start AND :end
    """)
    Integer getDailySales(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Tổng số đơn hôm nay
    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.orderTime BETWEEN :start AND :end
    """)
    Long getOrderCount(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Doanh thu theo giờ (24h)
    @Query("""
        SELECT HOUR(o.orderTime), COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.orderTime BETWEEN :start AND :end
        GROUP BY HOUR(o.orderTime)
    """)
    List<Object[]> getHourlySales(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Filter order theo type (EAT_IN / TAKE_AWAY)
    List<Order> findByOrderTimeBetweenAndOrderType(
            LocalDateTime start,
            LocalDateTime end,
            OrderType orderType
    );
}
