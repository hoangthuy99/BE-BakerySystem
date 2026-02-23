package com.ra.bakerysystem.service.impl;

import com.ra.bakerysystem.model.DTO.InventoryDTO;
import com.ra.bakerysystem.model.entity.Inventory;
import com.ra.bakerysystem.model.entity.Product;
import com.ra.bakerysystem.repository.InventoryRepository;
import com.ra.bakerysystem.repository.ProductRepository;
import com.ra.bakerysystem.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    @Transactional
    public void initMissingInventories() {
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            if (!inventoryRepository.existsByProductId(product.getId())) {

                Inventory inventory = Inventory.builder()
                        .product(product)
                        .currentQuantity(0)
                        .minThreshold(0)
                        .build();
                inventoryRepository.save(inventory);
            }
        }
    }


    @Transactional
    public void resetAllInventoryByCategory() {
        inventoryRepository.resetFoodInventory();
        inventoryRepository.resetDrinkInventory();
    }


    @Override
    public List<InventoryDTO> getAllInventory() {
        return inventoryRepository.findAll()
                .stream()
                .map(inv -> InventoryDTO.builder()
                        .inventoryId(inv.getInventoryId())
                        .productId(inv.getProduct().getId())
                        .productName(inv.getProduct().getName())
                        .imageUrl(inv.getProduct().getImageUrl())
                        .currentQuantity(inv.getCurrentQuantity())
                        .minThreshold(inv.getMinThreshold())
                        .lastUpdated(inv.getLastUpdated())
                        .build()
                )
                .toList();
    }

    @Override
    public Inventory adjustInventory(Long productId, Integer currentQuantity) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));


        inventory.setCurrentQuantity(currentQuantity);

        return inventoryRepository.save(inventory);
    }

    @Override
    public InventoryDTO updateThreshold(Long inventoryId, Integer minThreshold) {

        if (minThreshold == null || minThreshold < 0) {
            throw new IllegalArgumentException("minThreshold must be >= 0");
        }

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() ->
                        new RuntimeException("Inventory not found id=" + inventoryId)
                );

        inventory.setMinThreshold(minThreshold);
        Instant.now();

        Inventory saved = inventoryRepository.save(inventory);

        return InventoryDTO.builder()
                .productId(saved.getProduct().getId())
                .productName(saved.getProduct().getName())
                .imageUrl(saved.getProduct().getImageUrl())
                .currentQuantity(saved.getCurrentQuantity())
                .minThreshold(saved.getMinThreshold())
                .lastUpdated(saved.getLastUpdated())
                .build();
    }



}