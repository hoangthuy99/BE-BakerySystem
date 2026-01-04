package com.ra.bakerysystem.service.impl;

import com.ra.bakerysystem.model.DTO.DashboardResponseDTO;
import com.ra.bakerysystem.model.DTO.PopularProductDTO;
import com.ra.bakerysystem.repository.InventoryRepository;
import com.ra.bakerysystem.repository.OrderItemRepository;
import com.ra.bakerysystem.repository.OrderRepository;
import com.ra.bakerysystem.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public DashboardResponseDTO getDashboard() {

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);

        Integer dailySales = orderRepository.getDailySales(start, end);
        Long orderCount = orderRepository.getOrderCount(start, end);
        Long lowStockCount = inventoryRepository.countLowStock();

        // GỌI METHOD CHUNG (KHÔNG DUPLICATE LOGIC)
        List<Integer> hourlySales = getHourlySalesToday();

        // Top 5 products
        List<PopularProductDTO> popularProducts =
                orderItemRepository.findTopProducts(PageRequest.of(0, 5))
                        .stream()
                        .map(row -> new PopularProductDTO(
                                ((Number) row[0]).longValue(),
                                (String) row[1],
                                ((Number) row[2]).longValue()
                        ))
                        .toList();

        return DashboardResponseDTO.builder()
                .dailySales(dailySales)
                .orderCount(orderCount.intValue())
                .lowStockCount(lowStockCount.intValue())
                .hourlySales(hourlySales)
                .popularProducts(popularProducts)
                .build();
    }

    private List<Integer> getHourlySalesToday() {

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);

        List<Object[]> rawData = orderRepository.getHourlySales(start, end);

        // Khởi tạo mảng 24 giờ = 0
        List<Integer> hourlySales = new ArrayList<>(Collections.nCopies(24, 0));

        for (Object[] row : rawData) {
            Integer hour = ((Number) row[0]).intValue();
            Integer total = ((Number) row[1]).intValue();
            hourlySales.set(hour, total);
        }

        return hourlySales;
    }

}

