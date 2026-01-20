package com.ra.bakerysystem.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuggestedQuantityDTO {

    private Long productId;
    private int suggestedQuantity;

    // optional – để debug / hiển thị
    private int soldToday;
    private long totalSold;
    private double averagePerDay;
}
