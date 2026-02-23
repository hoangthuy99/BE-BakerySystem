package com.ra.bakerysystem.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.OffsetDateTime;

@Entity
@Table(name = "inventories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long inventoryId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @Column(name = "start_of_day_quantity")
    private Integer startOfDayQuantity;


    @Column(name = "current_quantity", nullable = false)
    private Integer currentQuantity;

    @Column(name = "min_threshold", nullable = false)
    private Integer minThreshold;

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @PrePersist
    protected void onCreate() {
        lastUpdated = Instant.now();
    }


}
