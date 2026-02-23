package com.ra.bakerysystem.service;

import com.ra.bakerysystem.common.OrderType;
import com.ra.bakerysystem.model.DTO.OrderRequestDTO;
import com.ra.bakerysystem.model.DTO.OrderResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO order);

    List<OrderResponseDTO> getOrdersByDate(LocalDate date, OrderType type);

    OrderResponseDTO getOrderById(Long id);


}

