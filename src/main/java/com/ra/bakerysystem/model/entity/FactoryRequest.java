package com.ra.bakerysystem.model.entity;

import com.ra.bakerysystem.common.FactoryRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

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

    /*
     * ========================= FIX =========================
     * delivered_quantity:
     *  - Số lượng đã giao thực tế
     *  - NOT NULL trong DB
     *  - Mặc định = 0 khi tạo request
     * ======================================================
     */
    @Column(name = "delivered_quantity", nullable = false)
    private Integer deliveredQuantity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "eta_at", nullable = false)
    private LocalDateTime etaAt;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FactoryRequestStatus status;
}
