package com.ra.bakerysystem.model.DTO;

import lombok.Builder;
import lombok.Data;


import java.util.List;

@Data
@Builder
public class DashboardResponseDTO {

    private Integer dailySales;
    private Integer orderCount;
    private Integer lowStockCount;

    private List<Integer> hourlySales;
    private List<PopularProductDTO> popularProducts;
}
