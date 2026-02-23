package com.ra.bakerysystem.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class PopularProductDTO {

    private Long productId;
    private String name;
    private Long soldQuantity;
}

