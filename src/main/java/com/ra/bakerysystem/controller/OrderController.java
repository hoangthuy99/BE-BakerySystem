package com.ra.bakerysystem.controller;

import com.ra.bakerysystem.common.OrderType;
import com.ra.bakerysystem.model.DTO.OrderRequestDTO;
import com.ra.bakerysystem.model.DTO.OrderResponseDTO;
import com.ra.bakerysystem.model.entity.Order;
import com.ra.bakerysystem.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/app/orders")
@RequiredArgsConstructor
@Tag(name = "Order API")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponseDTO createOrder(@RequestBody OrderRequestDTO dto) {
        return orderService.createOrder(dto);
    }

    @GetMapping
    public List<OrderResponseDTO> getOrders(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) OrderType type
    ) {
        LocalDate targetDate =
                date != null ? LocalDate.parse(date) : LocalDate.now();

        return orderService.getOrdersByDate(targetDate, type);
    }


    @GetMapping("/{id}")
    public OrderResponseDTO getOrderDetail(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }
}
