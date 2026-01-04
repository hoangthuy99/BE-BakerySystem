package com.ra.bakerysystem.model.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FactoryRequestDTO {
    private Long productId;
    private Integer requestQuantity;
    private LocalDateTime etaAt;
    private String note;
}
