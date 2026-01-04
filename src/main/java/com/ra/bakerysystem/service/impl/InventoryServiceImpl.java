package com.ra.bakerysystem.service.impl;

import com.ra.bakerysystem.model.DTO.InventoryDTO;
import com.ra.bakerysystem.model.entity.Inventory;
import com.ra.bakerysystem.repository.InventoryRepository;
import com.ra.bakerysystem.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory adjustInventory(Long productId, Integer currentQuantity) {

        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product_id=" + productId));

        inventory.setCurrentQuantity(currentQuantity);

        return inventoryRepository.save(inventory);
    }

}