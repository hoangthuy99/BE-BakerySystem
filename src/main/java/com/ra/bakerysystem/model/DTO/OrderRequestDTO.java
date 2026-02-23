package com.ra.bakerysystem.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ra.bakerysystem.common.OrderType;
import com.ra.bakerysystem.common.PaymentMethod;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private OrderType orderType;
    private PaymentMethod paymentMethod;
    private Integer totalAmount;
    private Integer paymentReceived;
    private Integer changeAmount;

    private List<OrderItemRequestDTO> items;
}