package com.ra.bakerysystem.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @Column(name = "product_id")
    @JsonProperty("product_id") // Khớp với Inventory interface trong types.ts
    private Long productId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    @JsonIgnore // Không trả về object Product ở đây để tránh vòng lặp JSON
    private Product product;

    @Column(name = "current_quantity", nullable = false)
    @JsonProperty("current_quantity")
    private Integer currentQuantity = 0;

    @Column(name = "min_threshold")
    @JsonProperty("min_threshold")
    private Integer minThreshold;

    @Column(name = "last_updated")
    @JsonProperty("last_updated")
    private Instant lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = Instant.now();
    }
}