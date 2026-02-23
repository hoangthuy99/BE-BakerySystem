package com.ra.bakerysystem.controller;

import com.ra.bakerysystem.model.DTO.InventoryDTO;
import com.ra.bakerysystem.model.entity.Inventory;
import com.ra.bakerysystem.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/app/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory API")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public List<InventoryDTO> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    // PATCH /api/v1/inventory/{productId}
    @PatchMapping("/{productId}")
    public Inventory adjustInventory(
            @PathVariable Long productId,
            @RequestParam(name = "currentQuantity", defaultValue = "0") Integer currentQuantity
    ) {

        log.info("Adjusting inventory quantity: {}", currentQuantity);
        return inventoryService.adjustInventory(productId, currentQuantity);
    }
    @PatchMapping("/{productId}/thresholds")
    public InventoryDTO updateThreshold(
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> body
    ) {
        Integer threshold = body.get("reorder_point");
        return inventoryService.updateThreshold(productId, threshold);
    }
    @PostMapping("/reset-daily")
    public ResponseEntity<?> resetDailyInventory() {

        inventoryService.resetAllInventoryByCategory();

        return ResponseEntity.ok("Daily inventory reset successfully");
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void autoResetInventory() {
        inventoryService.resetAllInventoryByCategory();
    }
    @PostMapping("/init-missing")
    public ResponseEntity<?> initMissingInventories() {
        inventoryService.initMissingInventories();
        return ResponseEntity.ok("Initialized missing inventories");
    }



}