package com.ra.bakerysystem.model.entity;

import com.ra.bakerysystem.common.FactoryRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "factory_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FactoryRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "request_quantity", nullable = false)
    private Integer requestQuantity;

    @Column(name = "delivered_quantity", nullable = false)
    private Integer deliveredQuantity;

    /**
     * ================= FIX BUG AUTO +10 =================
     * inventory_applied:
     *  - Đánh dấu request này đã cộng kho hay chưa
     *  - Giúp tránh cộng kho lặp khi reload / sync
     * ====================================================
     */
    @Column(name = "inventory_applied", nullable = false)
    private Boolean inventoryApplied;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "eta_at", nullable = false)
    private Instant etaAt;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FactoryRequestStatus status;
}