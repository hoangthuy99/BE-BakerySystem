package com.ra.bakerysystem.service;

import com.ra.bakerysystem.model.DTO.InventoryDTO;
import com.ra.bakerysystem.model.entity.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryService {

    List<InventoryDTO> getAllInventory();

    Inventory adjustInventory(Long productId, Integer currentQuantity);

    InventoryDTO updateThreshold(Long inventoryId, Integer minThreshold);

    void initMissingInventories();

    void resetAllInventoryByCategory();
}
