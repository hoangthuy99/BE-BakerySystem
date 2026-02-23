package com.ra.bakerysystem.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ra.bakerysystem.common.ProductType;
import jakarta.persistence.Column;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {
    private Long inventoryId;
    private Long productId;
    private String productName;
    private String imageUrl;
    private Integer currentQuantity;
    private Integer minThreshold;
    private Instant lastUpdated;
}

