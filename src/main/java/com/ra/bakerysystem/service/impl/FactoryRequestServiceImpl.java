package com.ra.bakerysystem.service.impl;

import com.ra.bakerysystem.common.FactoryRequestStatus;
import com.ra.bakerysystem.model.DTO.FactoryRequestDTO;
import com.ra.bakerysystem.model.entity.FactoryRequest;
import com.ra.bakerysystem.model.entity.Inventory;
import com.ra.bakerysystem.model.entity.Product;
import com.ra.bakerysystem.repository.FactoryRequestRepository;
import com.ra.bakerysystem.repository.InventoryRepository;
import com.ra.bakerysystem.repository.OrderItemRepository;
import com.ra.bakerysystem.repository.ProductRepository;
import com.ra.bakerysystem.service.FactoryRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // ✅ ADD
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j // ✅ ADD
@Service
@RequiredArgsConstructor
@Transactional
public class FactoryRequestServiceImpl implements FactoryRequestService {

    private final FactoryRequestRepository factoryRequestRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;

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

        LocalDateTime startOfToday = LocalDateTime.now()
                .toLocalDate()
                .atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1);

        int soldToday = orderItemRepository.getSoldInRange(
                productId,
                startOfToday,
                endOfToday
        );

        long totalSold = orderItemRepository.getTotalSold(productId);

        LocalDateTime firstSold = orderItemRepository.getFirstSoldDate(productId);
        LocalDateTime lastSold = orderItemRepository.getLastSoldDate(productId);

        if (firstSold == null || lastSold == null || totalSold == 0) {
            log.warn("⚠️ No sales history → default 10");
            return 10;
        }

        long days = Math.max(
                java.time.Duration.between(firstSold, lastSold).toDays() + 1,
                1
        );

        double averagePerDay = (double) totalSold / days;
        int suggested = (int) Math.ceil(averagePerDay - soldToday);

        log.info("📈 avg/day={}, soldToday={}, suggested={}",
                averagePerDay, soldToday, suggested);

        return Math.max(suggested, 10);
    }
}
