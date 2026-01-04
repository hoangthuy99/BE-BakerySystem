package com.ra.bakerysystem.service.impl;

import com.ra.bakerysystem.common.ProductType;
import com.ra.bakerysystem.common.OrderType;

import com.ra.bakerysystem.model.DTO.OrderItemRequestDTO;
import com.ra.bakerysystem.model.DTO.OrderRequestDTO;
import com.ra.bakerysystem.model.entity.*;
import com.ra.bakerysystem.repository.InventoryRepository;
import com.ra.bakerysystem.repository.OrderRepository;
import com.ra.bakerysystem.repository.ProductRepository;
import com.ra.bakerysystem.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public Order createOrder(OrderRequestDTO dto) {

        LocalDateTime now = LocalDateTime.now();

        // 1. Validate Eat-in
        if (dto.getOrderType() == OrderType.EAT_IN
                && now.toLocalTime().isAfter(LocalTime.of(20, 30))) {
            throw new RuntimeException("Eat-in is not allowed after 20:30");
        }

        // 2. Tạo Order entity

        Order order = new Order();
        order.setOrderType(dto.getOrderType());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setPaymentReceived(dto.getPaymentReceived());
        order.setItems(new ArrayList<>());

        int totalAmount = 0;

        // 3. Xử lý từng item

        for (OrderItemRequestDTO itemDTO : dto.getItems()) {

            // 3.1 Lấy product
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() ->
                            new RuntimeException("Product not found: " + itemDTO.getProductId())
                    );

            // 3.2 Check rượu trước 17h
            if (Boolean.TRUE.equals(product.getAlcoholic())
                    && now.toLocalTime().isBefore(LocalTime.of(17, 0))) {
                throw new RuntimeException("Alcohol is not allowed before 17:00");
            }

            // 3.3 Check & trừ kho
            if (product.getType() == ProductType.food
                    || product.getType() == ProductType.merchandise) {

                Inventory inventory = inventoryRepository.findById(product.getId())
                        .orElseThrow(() ->
                                new RuntimeException("Inventory not found for product: " + product.getId())
                        );

                if (inventory.getCurrentQuantity() < itemDTO.getQuantity()) {
                    throw new RuntimeException(
                            "Not enough stock for product: " + product.getName()
                    );
                }

                inventory.setCurrentQuantity(
                        inventory.getCurrentQuantity() - itemDTO.getQuantity()
                );
                inventoryRepository.save(inventory);
            }

            // 3.4 Snapshot OrderItem
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setName(product.getName());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(product.getPrice());

            order.getItems().add(item);

            totalAmount += product.getPrice() * itemDTO.getQuantity();
        }

        // 4. Tính tiền
        order.setTotalAmount(totalAmount);
        order.setChangeAmount(dto.getPaymentReceived() - totalAmount);

        // 5. Save

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date, OrderType type) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        if (type != null) {
            return orderRepository.findByOrderTimeBetweenAndOrderType(
                    start, end, type
            );
        }

        return orderRepository.findByOrderTimeBetween(start, end);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
