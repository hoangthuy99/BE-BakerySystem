package com.ra.bakerysystem.service.impl;

import com.ra.bakerysystem.common.FactoryRequestStatus;
import com.ra.bakerysystem.model.DTO.FactoryRequestDTO;
import com.ra.bakerysystem.model.entity.FactoryRequest;
import com.ra.bakerysystem.model.entity.Inventory;
import com.ra.bakerysystem.model.entity.OrderItem;
import com.ra.bakerysystem.model.entity.Product;
import com.ra.bakerysystem.repository.FactoryRequestRepository;
import com.ra.bakerysystem.repository.InventoryRepository;
import com.ra.bakerysystem.repository.OrderItemRepository;
import com.ra.bakerysystem.repository.ProductRepository;
import com.ra.bakerysystem.service.FactoryRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j // ✅ ADD
@Service
@RequiredArgsConstructor
@Transactional
public class FactoryRequestServiceImpl implements FactoryRequestService {

    private final FactoryRequestRepository factoryRequestRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;
//    private final SalesAverageCalculateRepository salesAverageCalculateRepository;


    @Value("${request.default:10}")
    private int defaultRequest;
    // =========================
    // CREATE FACTORY REQUEST
    // =========================
    @Override
    public FactoryRequest create(FactoryRequestDTO dto) {

        // 🔥 LOG QUAN TRỌNG NHẤT
        log.error("🚨 [CREATE FACTORY REQUEST] dto = {}", dto);
        log.error("🚨 CALL STACK:");
        for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
            log.error("    at {}", e);
        }

        Product product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        int finalRequestQuantity;

        if (Boolean.FALSE.equals(product.getAlcoholic())) {
            finalRequestQuantity = calculateAutoRequestQuantity(product.getId());
        } else {
            finalRequestQuantity = dto.getRequestQuantity();
        }

        FactoryRequest request = FactoryRequest.builder()
            .productId(product.getId())
            .productName(product.getName())
            .requestQuantity(finalRequestQuantity)
            .deliveredQuantity(0)
            .inventoryApplied(false)
            .etaAt(dto.getEtaAt())
            .note(dto.getNote())
            .status(FactoryRequestStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();

        log.error("🚨 SAVING FACTORY REQUEST: productId={}, qty={}",
            request.getProductId(),
            request.getRequestQuantity()
        );

        return factoryRequestRepository.save(request);
    }

    @Override
    public List<FactoryRequest> getAll() {
        return factoryRequestRepository.findAll();
    }

    @Override
    public FactoryRequest updateStatus(Long requestId, FactoryRequestStatus status) {

        log.info("🔄 updateStatus called: requestId={}, status={}", requestId, status);

        FactoryRequest request = factoryRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Factory request not found"));

        if (status == FactoryRequestStatus.DELIVERED
            && Boolean.FALSE.equals(request.getInventoryApplied())) {

            log.warn("📦 APPLY INVENTORY: productId={}, +{}",
                request.getProductId(),
                request.getRequestQuantity()
            );

            Inventory inventory = inventoryRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

            inventory.setCurrentQuantity(
                inventory.getCurrentQuantity() + request.getRequestQuantity()
            );
            inventoryRepository.save(inventory);

            request.setInventoryApplied(true);
        }

        request.setStatus(status);
        return factoryRequestRepository.save(request);
    }

    @Override
    public int getSuggestedQuantity(Long productId) {
        log.info("📊 getSuggestedQuantity called for productId={}", productId);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (Boolean.TRUE.equals(product.getAlcoholic())) {
            return 0;
        }

        return calculateAutoRequestQuantity(productId);
    }

    private int calculateAutoRequestQuantity(Long productId) {
        log.info("🧮 calculateAutoRequestQuantity(productId={})", productId);
        LocalDate today = LocalDate.now();
//        AverageType avgType;
//        LocalDate effectiveDate;
//        if (today.getMonthValue() == 1 && today.getDayOfMonth() == 1) {
//            avgType = AverageType.YEAR;
//            effectiveDate = today.minusYears(1).withDayOfYear(1);
//        } else if (today.getDayOfMonth() == 1) {
//            avgType = AverageType.MONTH;
//            effectiveDate = today.minusMonths(1).withDayOfMonth(1);
//        } else {
//            avgType = AverageType.DAY;
//            effectiveDate = today;
//        }
        // =========================
        // 2. Lấy snapshot trung bình
        // =========================
//        SaleAverageCalculateEntity saleAverageCalculate =
//            salesAverageCalculateRepository.findLatestAverageByDate(productId, avgType.name(), effectiveDate)
//                .orElse(null);
//        if (saleAverageCalculate == null) {
//            log.warn("⚠️ No average snapshot found → default {}",defaultRequest);
//            return defaultRequest;
//        }
//        int average;
//        int average = saleAverageCalculate.getAverageQuantity();
        List<OrderItem> orderItemsPast = orderItemRepository.getOrderItemsByOrderTime(null,productId);
        List<OrderItem> orderItemsToday = orderItemRepository.getOrderItemsByOrderTime(today,productId);

        if (CollectionUtils.isEmpty(orderItemsToday)) {
            log.warn("⚠️ Chưa có order nào với sản phẩm {} trong ngày hôm nay", productId);
            throw new RuntimeException("Chưa có order nào với sản phẩm " + productId + " trong ngày hôm nay đến thời điểm hiện tại");
        }
        int orderPast = orderItemsPast.stream().map(OrderItem::getQuantity).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        int orderToday = orderItemsToday.stream().map(OrderItem::getQuantity).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        if (orderPast < orderToday) {
            return defaultRequest;
        }
        int suggested = orderPast - orderToday;
        return Math.max(suggested, defaultRequest);
    }
}
