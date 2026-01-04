package com.ra.bakerysystem.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PopularProductDTO {

    private Long productId;
    private String name;
    private Long soldQuantity;
}

