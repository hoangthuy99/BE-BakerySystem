package com.ra.bakerysystem.model.entity;

import com.ra.bakerysystem.common.OrderType;
import com.ra.bakerysystem.common.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "order_time", nullable = false, updatable = false)
    private Instant orderTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderType orderType;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_received", nullable = false)
    private Integer paymentReceived;

    @Column(name = "change_amount", nullable = false)
    private Integer changeAmount;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items;

    @PrePersist
    public void onCreate() {
        orderTime = Instant.now();
    }
    public static String generateOrderCode() {
        return "OD" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
    }

}