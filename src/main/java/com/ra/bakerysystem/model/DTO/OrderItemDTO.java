package com.ra.bakerysystem.model.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class OrderItemDTO {

    private Long productId;
    private String name;
    private Integer quantity;
    private Integer unitPrice;
}

