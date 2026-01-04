package com.ra.bakerysystem.service;

import com.ra.bakerysystem.common.OrderType;
import com.ra.bakerysystem.model.DTO.OrderRequestDTO;
import com.ra.bakerysystem.model.entity.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    Order createOrder(OrderRequestDTO order);

    List<Order> getOrdersByDate(LocalDate date, OrderType type);

    Order getOrderById(Long id);

}

