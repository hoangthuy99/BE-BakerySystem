package com.ra.bakerysystem.model.DTO;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ra.bakerysystem.common.OrderType;
import com.ra.bakerysystem.common.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
@Data
@Builder
public class OrderResponseDTO {
    private Long id;
    private String orderTime;
    private String code;
    private OrderType orderType;
    private Integer totalAmount;
    private PaymentMethod paymentMethod;
    private Integer paymentReceived;
    private Integer changeAmount;
    private List<OrderItemDTO> items;
}
