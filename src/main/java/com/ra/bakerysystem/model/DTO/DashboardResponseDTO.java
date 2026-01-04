package com.ra.bakerysystem.model.DTO;

import lombok.Builder;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DashboardResponseDTO {

    private Integer dailySales;
    private Integer orderCount;
    private Integer lowStockCount;

    private List<Integer> hourlySales;
    private List<PopularProductDTO> popularProducts;
}
