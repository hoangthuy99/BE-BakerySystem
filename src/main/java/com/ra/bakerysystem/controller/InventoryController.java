package com.ra.bakerysystem.controller;

import com.ra.bakerysystem.model.entity.Inventory;
import com.ra.bakerysystem.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory API")
public class InventoryController {

    private final InventoryService inventoryService;

    // GET /api/v1/inventory
    @GetMapping
    @Operation(summary = "Get all inventory")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success")
    })
    public List<Inventory> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    // PATCH /api/v1/inventory/{productId}
    @PatchMapping("/{productId}")
    @Operation(summary = "Adjust inventory quantity")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public Inventory adjustInventory(
            @PathVariable Long productId,
            @RequestParam(name = "currentQuantity", defaultValue = "0") Integer currentQuantity
//            @RequestBody Map<String, Integer> body
    ) {

//            Integer currentQuantity = body.get("current_quantity") == null ? 0 : body.get("current_quantity");
        log.info("Adjusting inventory quantity: {}", currentQuantity);
        return inventoryService.adjustInventory(productId, currentQuantity);
    }
}