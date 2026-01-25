package com.ra.bakerysystem.repository;

import com.ra.bakerysystem.common.OrderType;
import com.ra.bakerysystem.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByOrderTimeBetween(
            Instant start,
            Instant end
    );

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

    // Filter order theo type (EAT_IN / TAKE_AWAY)
    List<Order> findByOrderTimeBetweenAndOrderType(
        Instant start,
        Instant end,
            OrderType orderType
    );
}
