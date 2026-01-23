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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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


        return factoryRequestRepository.save(request);
    }

    @Override
    public List<FactoryRequest> getAll() {
        return factoryRequestRepository.findAll();
    }

    @Override
    public FactoryRequest updateStatus(Long requestId, FactoryRequestStatus status) {

        FactoryRequest request = factoryRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Factory request not found"));

        if (status == FactoryRequestStatus.DELIVERED
                && Boolean.FALSE.equals(request.getInventoryApplied())) {


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

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (Boolean.TRUE.equals(product.getAlcoholic())) {
            return 0;
        }

        return calculateAutoRequestQuantity(productId);
    }

    private int calculateAutoRequestQuantity(Long productId) {

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
            return 10;
        }

        long days = Math.max(
                java.time.Duration.between(firstSold, lastSold).toDays() + 1,
                1
        );

        double averagePerDay = (double) totalSold / days;
        int suggested = (int) Math.ceil(averagePerDay - soldToday);

        return Math.max(suggested, 10);
    }
}
