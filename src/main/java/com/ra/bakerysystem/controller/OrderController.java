package com.ra.bakerysystem.controller;

import com.ra.bakerysystem.common.OrderType;
import com.ra.bakerysystem.model.DTO.OrderRequestDTO;
import com.ra.bakerysystem.model.entity.Order;
import com.ra.bakerysystem.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order API")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create order")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public Order createOrder(@RequestBody OrderRequestDTO dto) {
        return orderService.createOrder(dto);
    }

    @GetMapping
    @Operation(summary = "Get orders by date")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success")
    })
    public List<Order> getOrders(
            @RequestParam String date,
            @RequestParam(required = false) OrderType type
    ) {
        return orderService.getOrdersByDate(
                LocalDate.parse(date),
                type
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order detail")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public Order getOrderDetail(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }
}
