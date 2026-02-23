package com.ra.bakerysystem.model.DTO;


import lombok.Data;
@Data
public class OrderItemRequestDTO {
    private Long productId;
    private Integer quantity;
}
