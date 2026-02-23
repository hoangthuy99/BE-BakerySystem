package com.ra.bakerysystem.model.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PosDTO {
    private ProductDTO product;
    private InventoryDTO inventory;
}
