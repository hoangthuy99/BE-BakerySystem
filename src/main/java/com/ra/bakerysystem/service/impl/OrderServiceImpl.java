package com.ra.bakerysystem.service.impl;

import com.ra.bakerysystem.common.OrderType;

import com.ra.bakerysystem.model.DTO.OrderItemDTO;
import com.ra.bakerysystem.model.DTO.OrderItemRequestDTO;
import com.ra.bakerysystem.model.DTO.OrderRequestDTO;
import com.ra.bakerysystem.model.DTO.OrderResponseDTO;
import com.ra.bakerysystem.model.entity.*;
import com.ra.bakerysystem.repository.InventoryRepository;
import com.ra.bakerysystem.repository.OrderRepository;
import com.ra.bakerysystem.repository.ProductRepository;
import com.ra.bakerysystem.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ZoneId businessZone;
    private final Clock clock;
    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order items must not be empty");
        }

        Order order = new Order();
        order.setOrderTime(Instant.now(clock));
        order.setCode(Order.generateOrderCode());
        order.setOrderType(request.getOrderType());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentReceived(request.getPaymentReceived());

        List<OrderItem> orderItems = new ArrayList<>();
        int totalAmount = 0;

        for (OrderItemRequestDTO itemReq : request.getItems()) {

            //  Validate productId
            if (itemReq.getProductId() == null) {
                throw new IllegalArgumentException("Product id must not be null");
            }

            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));

            //  Check tồn kho
            if (inventory.getCurrentQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException(
                        "Not enough stock for product: " + product.getName()
                );
            }

            //  TRỪ KHO
            inventory.setCurrentQuantity(
                    inventory.getCurrentQuantity() - itemReq.getQuantity()
            );

            inventoryRepository.save(inventory);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setName(product.getName());
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(product.getPrice());

            totalAmount += item.getQuantity() * item.getUnitPrice();
            orderItems.add(item);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setChangeAmount(
                request.getPaymentReceived() - totalAmount
        );
        System.out.println("Server UTC: " + Instant.now(clock));
        System.out.println("VN time: " +
                ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        Order savedOrder = orderRepository.save(order);



        return mapToResponseDTO(savedOrder);
    }



    @Override
    public List<OrderResponseDTO> getOrdersByDate(LocalDate date, OrderType type) {

        Instant start = date
                .atStartOfDay(businessZone)
                .toInstant();

        Instant end = date
                .atTime(23, 59, 59)
                .atZone(businessZone)
                .toInstant();

        List<Order> orders;

        if (type != null) {
            orders = orderRepository.findOrdersByDateAndType(start, end, type);
        } else {
            orders = orderRepository.findOrdersByDate(start, end);
        }

        return orders.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }


    @Override
    public OrderResponseDTO getOrderById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Order id must not be null");
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return mapToResponseDTO(order);
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
        String vnTime = order.getOrderTime()
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return OrderResponseDTO.builder()
                .id(order.getId())
                .code(order.getCode())
                .orderTime(vnTime) // đã convert VN
                .orderType(order.getOrderType())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentReceived(order.getPaymentReceived())
                .changeAmount(order.getChangeAmount())
                .items(
                        order.getItems().stream()
                                .map(i -> OrderItemDTO.builder()
                                        .productId(i.getProduct().getId())
                                        .name(i.getName())
                                        .quantity(i.getQuantity())
                                        .unitPrice(i.getUnitPrice())
                                        .build()
                                )
                                .toList()
                )
                .build();
    }

}
