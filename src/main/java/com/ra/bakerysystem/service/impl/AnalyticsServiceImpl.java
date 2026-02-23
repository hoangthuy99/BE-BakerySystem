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

import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final ZoneId businessZone;

    @Override
    public DashboardResponseDTO getDashboard() {


        LocalDate today = LocalDate.now(businessZone);

        Instant start = today
                .atStartOfDay(businessZone)
                .toInstant();

        Instant end = today
                .atTime(23, 59, 59)
                .atZone(businessZone)
                .toInstant();

        Integer dailySales = orderRepository.getDailySales(start, end);
        Long orderCount = orderRepository.getOrderCount(start, end);
        Long lowStockCount = inventoryRepository.countLowStock();

        List<Integer> hourlySales = getHourlySales(today);

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
                .dailySales(dailySales != null ? dailySales : 0)
                .orderCount(orderCount != null ? orderCount.intValue() : 0)
                .lowStockCount(lowStockCount != null ? lowStockCount.intValue() : 0)
                .hourlySales(hourlySales)
                .popularProducts(popularProducts)
                .build();
    }

    private List<Integer> getHourlySales(LocalDate date) {

        Instant start = date
                .atStartOfDay(businessZone)
                .toInstant();

        Instant end = date
                .atTime(23, 59, 59)
                .atZone(businessZone)
                .toInstant();

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

