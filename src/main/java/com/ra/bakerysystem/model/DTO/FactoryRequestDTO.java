package com.ra.bakerysystem.model.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class FactoryRequestDTO {
    @NotNull(message = "productId is required")
    private Long productId;
    private Integer deliveredQuantity;

    @NotNull(message = "quantity is required")
    private Integer quantity;

    @NotNull(message = "etaAt is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime etaAt;

    private String note;
}
